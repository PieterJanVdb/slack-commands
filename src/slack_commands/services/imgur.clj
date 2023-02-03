(ns slack-commands.services.imgur
  (:require [clj-http.client :as client]
            [environ.core :refer [env]]))

(defn- request [encoded-image]
  (let [client-id (env :imgur-client-id)
        options {:form-params {:image encoded-image :type "base64"}
                 :headers {"Authorization" (str "Client-ID " client-id)}
                 :as :json}
        {:keys [body]} (client/post "https://api.imgur.com/3/upload" options)]
    body))

(defn upload [encoded-image]
  (try
    (let [body (request encoded-image)
          {{link :link} :data} body]
      link)
    (catch Exception ex
      (.printStackTrace ex)
      (throw (ex-info "Could not upload image" {:cause :imgur-error})))))
