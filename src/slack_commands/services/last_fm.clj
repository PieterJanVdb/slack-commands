(ns slack-commands.services.last-fm
  (:require [clj-http.client :as client]
            [environ.core :refer [env]]))

(defn- query [username api-key]
  (client/get
   "http://ws.audioscrobbler.com/2.0"
   {:query-params {"method" "user.getrecenttracks"
                   "user" username
                   "api_key" api-key
                   "format" "json"
                   "limit" 1}
    :as :json-strict}))

(defn- true-string? [s] (= s "true"))

(defn- get-large-thumbnail [images]
  ((keyword "#text") (some #(when (= "large" (:size %)) %) images)))

(defn get-track [username]
  (try
    (let [res (query username (env :last-fm-api-key))
          {{{[track] :track} :recenttracks} :body} res]
      (if track
        {:artist (get-in track [:artist (keyword "#text")])
         :album (get-in track [:album (keyword "#text")])
         :name (:name track)
         :thumbnail (get-large-thumbnail (:image track))
         :now-playing (true-string? (get-in track [(keyword "@attr") :nowplaying]))}
        nil))
    (catch Exception ex
      (.printStackTrace ex)
      (throw (ex-info (str "Could not fetch scrobbles for " username) {:cause :last-fm-error})))))