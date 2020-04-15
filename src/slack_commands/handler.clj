(ns slack-commands.handler
  (:require [compojure.core :refer [defroutes wrap-routes POST]]
            [compojure.route :as route]
            [slack-commands.middleware.verify :refer [wrap-verify-signature]]
            [slack-commands.middleware.body-string :refer [wrap-body-string]]
            [slack-commands.middleware.error :refer [wrap-exception]]
            [slack-commands.services.last-fm :refer [get-track]]
            [slack-commands.services.spotify :refer [get-spotify-link]]
            [slack-commands.format :refer [format-np]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [clojure.string :refer [split]]))

(defn get-username [text]
  (let [username (first (split text #" "))]
    (if (empty? username) nil username)))

(defn handle-np [username]
  (if-let [{:keys [artist name] :as track} (get-track username)]
    (let [link (get-spotify-link name artist)
          msg (format-np (assoc track :username username :link link))]
      msg)
    (throw (ex-info (str "Could not get track for " username) {:cause :np-error}))))

(defroutes app-routes
  (-> (POST "/np" [text]
        (if-let [username (and text (get-username text))]
          (response (handle-np username))
          (throw (ex-info "Please provide a username" {:cause :bad-input}))))
      (wrap-routes wrap-verify-signature))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      wrap-body-string
      wrap-exception
      wrap-json-response))