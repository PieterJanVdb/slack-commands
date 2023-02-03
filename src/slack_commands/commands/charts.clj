(ns slack-commands.commands.charts
  (:require [java-time :refer [instant to-millis-from-epoch]]))

(defn get-ts []
  (to-millis-from-epoch (instant)))

(defn format-chart [url]
  {:success true :msg {:response_type "in_channel" :text url}})

(defn handle-one-week [username]
  (let [now (get-ts)]
    (format-chart (str "http://collage.cx/" username ".png?" now))))

(defn handle-one-month [username]
  (format-chart (str "http://collage.cx/" username "/1month.png")))