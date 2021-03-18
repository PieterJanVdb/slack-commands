(ns slack-commands.commands.wtf
  (:require [clojure.java.io :as io]
            [slack-commands.services.imgur :refer [upload]]
            [slack-commands.error :refer [get-error-message]])
  (:import [javax.imageio ImageIO]
           [java.awt Font]
           [java.io ByteArrayOutputStream]
           [java.awt.image BufferedImage]
           [java.util Base64]))

(def MIN_OFFSET 2)
(def OFFSET_STEP 22)
(def MAX_LENGTH 9)
(def TEMPLATE "template.jpg")
(def OUTPUT_FORMAT "jpg")
(def VAL_ERROR
  (str "Please provide a name shorter than or equal to " MAX_LENGTH " characters"))

(defn get-template []
  (io/resource TEMPLATE))

(defn valid-name? [name]
  (<= (count name) MAX_LENGTH))

(defn get-x-offset [name]
  (let [len (count name)]
    (+ MIN_OFFSET (* OFFSET_STEP (- MAX_LENGTH len)))))

(defn draw-name [^BufferedImage image name]
  (let [graphics (.createGraphics image)
        font (Font. "Arial" Font/PLAIN 35)
        x-offset (get-x-offset name)]
    (println "drawing on image...")
    (.setFont graphics font)
    (.drawString graphics name x-offset 381)
    (println "done drawing on image!")
    image))

(defn image->base64 [image]
  (with-open [baos (ByteArrayOutputStream.)]
      (ImageIO/write image OUTPUT_FORMAT baos)
      (.encodeToString (Base64/getEncoder) (.toByteArray baos))))

(defn generate-image [name]
  (println "generating image...")
  (-> (get-template)
      ImageIO/read
      (draw-name name)))

(defn get-image-link [name]
  (-> name
      generate-image
      image->base64
      upload))

(defn format-wtf [name link]
  {:type "image"
   :alt_text (str name " what the fuck are you talking about")
   :image_url link})

(defn handle-wtf [name]
  (try
    (if (valid-name? name)
      (let [link (get-image-link name)
            msg (format-wtf name link)]
        (println "msg" msg)
        {:success true :msg msg})
      (throw (ex-info VAL_ERROR {:cause :bad-input})))
    (catch clojure.lang.ExceptionInfo ex
      {:error true :msg (get-error-message ex)})
    (catch Exception ex
      (println (.getMessage ex))
      {:error true :msg (get-error-message ex)})))
