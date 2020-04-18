(ns slack-commands.commands.charts
  (:require [clj-time.core :as t]
            [clj-time.coerce :as c]))

(defn get-ts []
  (c/to-long (t/now)))

(defn handle-one-week [username]
  (let [now (get-ts)]
    {:text (str "http://collage.cx/" username ".png?" now)}))

(defn handle-one-month [username]
  (let [now (get-ts)]
    {:text (str "http://collage.cx/" username "/1month.png?" now)}))