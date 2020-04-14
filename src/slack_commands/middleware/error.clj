(ns slack-commands.middleware.error
  (:require [slack-commands.format :refer [format-error]]
            [slack-commands.error :refer [get-error-message]]
            [ring.util.response :refer [response]]))

(defn wrap-exception
  [handler]
  (fn [request]
    (try
      (handler request)
      (catch clojure.lang.ExceptionInfo e
        (response (format-error (get-error-message e)))))))