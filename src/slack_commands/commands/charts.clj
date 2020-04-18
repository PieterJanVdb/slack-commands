(ns slack-commands.commands.charts
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defn get-ts []
  (c/to-long (t/now)))

(defn format-chart [url]
  {:success true :msg {:response_type "in_channel" :text url}})

(defn handle-one-week [username]
  (let [now (get-ts)]
    (format-chart (str "http://collage.cx/" username ".png?" now))))

(defn handle-one-month [username]
  (format-chart (str "http://collage.cx/" username "/1month.png?")))