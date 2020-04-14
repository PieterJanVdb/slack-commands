(ns slack-commands.services.spotify
  (:require [clj-spotify.util :refer [get-access-token]]
            [clj-spotify.core :refer [search]]
            [environ.core :refer [env]]
            [clojure.string]))

(defn get-spotify-link [track artist]
  (let [access-token (get-access-token (env :spotify-client) (env :spotify-secret))
        params {:q (str "track:" track " artist:" artist) :type "track"}
        {{[{{link :spotify} :external_urls}] :items} :tracks} (search params access-token)]
    link))