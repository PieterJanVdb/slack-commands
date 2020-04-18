(ns slack-commands.format)

(defn format-error [text]
  {:text text :color "danger" :mrkdwn-in ["text"]})

