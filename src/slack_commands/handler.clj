(ns slack-commands.handler
  (:require [compojure.core :refer [defroutes wrap-routes POST]]
            [compojure.route :as route]
            [clj-http.client :as client]
            [slack-commands.middleware.verify :refer [wrap-verify-signature]]
            [slack-commands.middleware.body-string :refer [wrap-body-string]]
            [slack-commands.middleware.error :refer [wrap-exception]]
            [slack-commands.middleware.username :refer [wrap-get-username]]
            [slack-commands.format :refer [format-error]]
            [slack-commands.commands.np :refer [handle-np]]
            [slack-commands.commands.charts :refer [handle-one-week handle-one-month]]
            [slack-commands.commands.wtf :refer [handle-wtf]]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]))

(defn respond [url msg]
  (client/post url {:content-type :json :form-params msg}))

(defn execute-command [response_url command & args]
  (future
    (let [{:keys [success msg]} (apply command args)]
      (respond response_url (if success msg (format-error msg)))))
  {:status 200 :body "Running..."})

(defroutes commands
  (POST "/np" [response_url :as {username :username}]
    (execute-command response_url handle-np username))
  (POST "/1week" [response_url :as {username :username}]
    (execute-command response_url handle-one-week username))
  (POST "/1month" [response_url :as {username :username}]
    (execute-command response_url handle-one-month username))
  (POST "/wtf" [user_name response_url :as {name :username}]
    (execute-command response_url handle-wtf name user_name)))

(defroutes app-routes
  (-> commands
      wrap-get-username
      (wrap-routes wrap-verify-signature))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults api-defaults)
      wrap-body-string
      wrap-exception
      wrap-json-response))