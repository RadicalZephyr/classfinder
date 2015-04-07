(ns classfinder.scrape
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(defn fetch-url [url req-opts]
  (when (:method req-opts)
    (some-> req-opts
            (assoc :url url)
            client/request
            :body
            html/html-snippet)))
