(ns slack-commands.format)

(defn format-error [text]
  {:text text :color "danger" :mrkdwn-in ["text"]})

(defn get-text [username now-playing]
  (let [link (str "https://www.last.fm/user/" username)]
    (str "<" link "|*" username "*> "
         (if now-playing
           "is currently listening to: "
           "has last listened to: "))))

(defn get-spotify-text [link]
  (if link (str "<" link "|Listen on Spotify...>") "No stream found..."))

(defn format-np [{:keys [username now-playing link artist album name thumbnail]}]
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