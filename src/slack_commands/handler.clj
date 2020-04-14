(ns slack-commands.handler
  (:require [compojure.core :refer [defroutes wrap-routes POST]]
            [compojure.route :as route]
            [clj-http.client :as client]
            [slack-commands.middleware.verify :refer [wrap-verify-signature]]
            [slack-commands.middleware.body-string :refer [wrap-body-string]]
            [slack-commands.middleware.error :refer [wrap-exception]]
            [slack-commands.services.last-fm :refer [get-track]]
            [slack-commands.services.spotify :refer [get-spotify-link]]
            [slack-commands.error :refer [get-error-message]]
            [slack-commands.format :refer [format-np format-error]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [clojure.string :refer [split]]))

(defn get-username [text]
  (let [username (first (split text #" "))]
    (if (empty? username) nil username)))

(defn respond [url msg]
  (client/post url {:content-type :json :form-params msg}))

(defn handle-np [username]
  (try
    (if-let [{:keys [artist name] :as track} (get-track username)]
      (let [link (get-spotify-link name artist)
            msg (format-np (assoc track :username username :link link))]
        {:success true :msg msg})
      {:error true :msg (str "Could not fetch track for " username)})
    (catch clojure.lang.ExceptionInfo ex
      {:error true :msg (get-error-message ex)})
    (catch Exception ex
      (println (.getMessage ex))
      {:error true :msg (get-error-message ex)})))

(defroutes app-routes
  (-> (POST "/np" [text response_url]
        (if-let [username (and text (get-username text))]
          (do
            (future
              (let [{:keys [success msg]} (handle-np username)]
                (respond response_url (if success msg (format-error msg)))))
            {:status 200})
          (throw (ex-info "Please provide a usename" {:cause :bad-input}))))
      (wrap-routes wrap-verify-signature))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      wrap-body-string
      wrap-exception
      wrap-json-response))