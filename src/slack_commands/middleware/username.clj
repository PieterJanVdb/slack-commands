(ns slack-commands.middleware.username
  (:require [clojure.string :refer [split]]))

(defn- get-username [text]
  (let [username (first (split text #" "))]
    (if (empty? username) nil username)))

(defn wrap-get-username [handler]
  (fn [request]
    (let [{text :text} (:params request)]
      (if-let [username (and text (get-username text))]
        (handler (assoc request :username username))
        (throw (ex-info "Please provide a username" {:cause :bad-input}))))))