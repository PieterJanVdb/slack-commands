(ns slack-commands.middleware.body-string
  (:require [ring.util.request :refer [body-string]])
  (:import (java.io ByteArrayInputStream)))

(defn- string->stream [s]
  (-> s
      (.getBytes "UTF-8")
      (ByteArrayInputStream.)))

(defn wrap-body-string [handler]
  (fn [request]
    (let [body-str (body-string request)]
      (handler
       (assoc request
              :body (string->stream body-str)
              :body-str body-str)))))