(ns slack-commands.middleware.argument
  (:require [clojure.string :refer [split]]))

(defn- get-username [text]
  (let [argument (first (split text #" "))]
    (if (empty? argument) nil argument)))

(defn wrap-get-username [handler]
  (fn [request]
    (let [{text :text} (:params request)]
      (if-let [username (and text (get-username text))]
        (handler (assoc request :username username))
        (throw (ex-info "Please provide a username" {:cause :bad-input}))))))

(defn wrap-get-argument [handler]
  (fn [request]
    (let [{text :text} (:params request)]
      (if-let [argument (if (empty? text) nil text)]
        (handler (assoc request :argument argument))
        (throw (ex-info "Please provide an argument" {:cause :bad-input}))))))
