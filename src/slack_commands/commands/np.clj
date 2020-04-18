(ns slack-commands.commands.np
  (:require [slack-commands.services.last-fm :refer [get-track]]
            [slack-commands.services.spotify :refer [get-spotify-link]]
            [slack-commands.error :refer [get-error-message]]))

(defn- get-text [username now-playing]
  (let [link (str "https://www.last.fm/user/" username)]
    (str "<" link "|*" username "*> "
         (if now-playing
           "is currently listening to: "
           "has last listened to: "))))

(defn- get-spotify-text [link]
  (if link (str "<" link "|Listen on Spotify...>") "No stream found..."))

(defn- format-np [{:keys [username now-playing link artist album name thumbnail]}]
  (let [spotify-text (get-spotify-text link)
        text (get-text username now-playing)]
    {:text text
     :response_type "in_channel"
     :attachments [{:thumb_url thumbnail
                    :mrkdwn_in ["text"]
                    :fields [{:title "Artist" :value artist :short true}
                             {:title "Name" :value name :short true}
                             {:title "Album" :value album :short true}
                             {:title "Stream" :value spotify-text :short true}]}]}))

(defn handle-np [username]
  (try
    (if-let [{:keys [artist name] :as track} (get-track username)]
      (let [link (get-spotify-link name artist)
            msg (format-np (assoc track :username username :link link))]
        {:success true :msg msg})
      {:error true :msg (str "Could not fetch track for " username)})
    (catch clojure.lang.ExceptionInfo ex
      {:error true :msg (get-error-message ex)})
    (catch Exception ex
      (println (.getMessage ex))
      {:error true :msg (get-error-message ex)})))
