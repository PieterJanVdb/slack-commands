(ns slack-commands.services.weather
  (:require [clj-http.client :as client]
            [clojure.string :as str]
            [environ.core :refer [env]]))

(def icon-emoji-map
  {"01d" ":sunny:"
   "02d" ":partly_sunny:"
   "03d" ":cloud:"
   "04d" ":cloud:"
   "09d" ":rain_cloud:"
   "10d" ":partly_sunny_rain:"
   "11d" ":thunder_cloud_and_rain:"
   "13d" ":snowflake:"
   "50d" ":fog:"
   "01n" ":new_moon:"
   "02n" ":cloud:"
   "03n" ":cloud:"
   "04n" ":cloud:"
   "09n" ":rain_cloud:"
   "10n" ":rain_cloud:"
   "11n" ":thunder_cloud_and_rain"
   "13n" ":snowflake:"
   "50n" ":fog:"})

(defn- c->f [temperature]
  (+ 32 (* 1.8 temperature)))

(defn- request-geo [query]
  (client/get
   "http://api.openweathermap.org/geo/1.0/direct"
   {:query-params {"q" query
                   "limit" 1
                   "appid" (env :open-weather-api-key)}
    :as :json}))

(defn- request-current-weather [[lat lon]]
  (client/get
   "https://api.openweathermap.org/data/2.5/weather"
   {:query-params {"lat" lat
                   "lon" lon
                   "units" "metric"
                   "appid" (env :open-weather-api-key)}
    :as :json}))

(defn- get-location [query]
  (let [{body :body} (request-geo query)
        {:keys [lat lon country name]} (first body)]
    {:country-emoji (str ":flag-" (str/lower-case country) ":")
     :coordinates [lat lon]
     :name name}))

(defn- get-current-weather [coordinates]
  (let [{body :body} (request-current-weather coordinates)]
    {:description (get-in body [:weather 0 :description])
     :temperature (get-in body [:main :temp])
     :feels-like  (get-in body [:main :feels_like])
     :humidity    (get-in body [:main :humidity])
     :weather-emoji (get icon-emoji-map (get-in body [:weather 0 :icon]))}))

(defn get-current-weather-by-query [query]
  (let [location (get-location query)
        {:keys [temperature feels-like] :as weather} (get-current-weather (:coordinates location))]
    (merge location
           (assoc weather
                  :temperature  {:c (int temperature) :f (int (c->f temperature))}
                  :feels-like {:c (int feels-like) :f (int (c->f feels-like))}))))
