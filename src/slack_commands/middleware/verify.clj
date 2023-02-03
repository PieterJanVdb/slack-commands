(ns slack-commands.middleware.verify
  (:require [java-time :refer [instant to-millis-from-epoch]]
            [environ.core :refer [env]]
            [ring.util.response :refer [response status]])
  (:import (javax.crypto Mac)
           (javax.crypto.spec SecretKeySpec)
           (org.apache.commons.codec.binary Hex)))

(def signing-algorithm "HMACSHA256")

(defn- valid-timestamp? [timestamp]
  (let [now (to-millis-from-epoch (instant))]
    (> (- now timestamp) (* 60 5))))

(defn- get-signature [body timestamp]
  (str "v0:" timestamp ":" body))

(defn- get-signing-key [secret]
  (SecretKeySpec. (.getBytes secret) signing-algorithm))

(defn- get-mac [signing-key]
  (doto (Mac/getInstance signing-algorithm)
    (.init signing-key)))

(defn- get-hashed [key signature]
  (let [mac (get-mac (get-signing-key key))]
    (str "v0="
         (Hex/encodeHexString (.doFinal mac (.getBytes signature))))))

(defn- is-valid-request [body timestamp signature]
  (let [is-valid-timestamp (valid-timestamp? timestamp)
        hashed (get-hashed (env :slack-signing-secret) (get-signature body timestamp))
        is-valid-signature (= hashed signature)]
    (and is-valid-timestamp is-valid-signature)))

(defn wrap-verify-signature [handler]
  (fn [request]
    (let [body-str (:body-str request)
          headers (:headers request)
          timestamp (get headers "x-slack-request-timestamp")
          signature (get headers "x-slack-signature")]
      (if (and timestamp
               signature
               (is-valid-request body-str (read-string timestamp) signature))
        (handler request)
        (-> (response "Access Denied")
            (status 403))))))
