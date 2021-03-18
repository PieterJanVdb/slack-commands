(ns slack-commands.commands.wtf
  (:require [clojure.java.io :as io]
            [slack-commands.services.imgur :refer [upload]]
            [slack-commands.error :refer [get-error-message]]
            [mikera.image.core :refer [resize]])
  (:import [javax.imageio ImageIO]
           [java.awt Font]
           [java.io ByteArrayOutputStream]
           [java.awt.image BufferedImage]
           [java.util Base64]))

(def MIN_OFFSET 2)
(def OFFSET_STEP 22)
(def MAX_LENGTH 9)
(def TEMPLATE "template.jpg")
(def TEMPLATE_WIDTH 350)
(def TEXT_Y_OFFSET 381)
(def FONT "Arial")
(def FONT_SIZE 35)
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
        font (Font. FONT Font/PLAIN FONT_SIZE)
        x-offset (get-x-offset name)]
    (.setFont graphics font)
    (.drawString graphics name x-offset TEXT_Y_OFFSET)
    image))

(defn image->base64 [image]
  (with-open [baos (ByteArrayOutputStream.)]
    (ImageIO/write image OUTPUT_FORMAT baos)
    (.encodeToString (Base64/getEncoder) (.toByteArray baos))))

(defn generate-image [name]
  (-> (get-template)
      ImageIO/read
      (draw-name name)
      (resize TEMPLATE_WIDTH)))

(defn get-image-link [name]
  (-> name
      generate-image
      image->base64
      upload))

(defn format-wtf [name user-id link]
  {:response_type "in_channel"
   :blocks [{:type "image"
             :alt_text (str name " what the fuck are you talking about")
             :image_url link}
            {:type "context"
             :elements [{:type "plain_text" :text (str "By: <@" user-id ">")}]}]})

(defn handle-wtf [name user-id]
  (try
    (if (valid-name? name)
      (let [link (get-image-link name)
            msg (format-wtf name user-id link)]
        {:success true :msg msg})
      (throw (ex-info VAL_ERROR {:cause :bad-input})))
    (catch clojure.lang.ExceptionInfo ex
      {:error true :msg (get-error-message ex)})
    (catch Exception ex
      (println (.getMessage ex))
      {:error true :msg (get-error-message ex)})))
