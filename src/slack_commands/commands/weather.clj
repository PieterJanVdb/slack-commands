(ns slack-commands.commands.weather
  (:require [slack-commands.services.weather :refer [get-current-weather-by-query]]
            [slack-commands.error :refer [get-error-message]]))

(defn- format-weather [weather]
  (let [{:keys [country-emoji name description temperature feels-like weather-emoji]} weather]
    {:text (str name " " country-emoji
                " — _" description "_ " weather-emoji "\n*"
                (:c temperature) " °C / " (:f temperature)
                " °F* (feels like " (:c feels-like)
                " °C / " (:f feels-like) " °F)")
     :response_type "in_channel"
     :attachments [{:mrkdwn_in ["text"]}]}))

(defn handle-weather [query]
  (try
    (if-let [weather (get-current-weather-by-query query)]
      (let [msg (format-weather weather)]
        {:success true :msg msg})
      {:error true :msg (str "Could not retrieve weather info for " query)})
    (catch clojure.lang.ExceptionInfo ex
      {:error true :msg (get-error-message ex)})
    (catch Exception ex
      {:error true :msg (get-error-message ex)})))
