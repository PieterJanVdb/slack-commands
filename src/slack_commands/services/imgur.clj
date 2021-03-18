(ns slack-commands.services.imgur
  (:require [org.httpkit.client :as http]
            [environ.core :refer [env]]
            [clojure.data.json :as json]))

(defn query
  "For some reason I had to use another http client here
   to upload the image. Should look into why but for the
   meantime this will do."
  [encoded-image]
  (let [client-id (env :imgur-client-id)
        options {:form-params {:image encoded-image :type "base64"}
                 :headers {"Authorization" (str "Client-ID " client-id)}}
        {:keys [body error]} @(http/post "https://api.imgur.com/3/upload" options)]
    (if error
      (throw error)
      (json/read-str body :key-fn keyword))))

(defn upload [encoded-image]
  (println "length base64: " (count (encoded-image)))
  (try
    (let [body (query encoded-image)
          {{link :link} :data} body]
      link)
    (catch Exception ex
      (.printStackTrace ex)
      (throw (ex-info "Could not upload image" {:cause :imgur-error})))))
