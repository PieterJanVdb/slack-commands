(ns slack-commands.error)

(defn get-error-message [e]
  (if (:cause (ex-data e)) (.getMessage e) "Something went wrong"))
