(ns classfinder.scrape
  (:require [clj-http.client :as client]
            [net.cgrand.enlive-html :as html]))

(def base-url "https://admin.wwu.edu/pls/wwis/wwsktime.SelClass")

(defn fetch-url [url req-opts]
  (when (:method req-opts)
    (some-> req-opts
            (assoc :url url)
            client/request
            :body
            html/html-snippet)))

(defn option->map [{{id :value} :attrs
                    [name] :content}]
  {:id id
   :name name})

(defn get-list [select-name]
  (map option->map
       (-> base-url
           (fetch-url {:method :get})
           (html/select [[:select
                          (html/attr= :name select-name)]
                         :option]))))

(defn get-terms []
  (get-list "term"))

(defn get-subjects []
  (drop 1 (get-list "sel_subj")))

(defn get-course-attributes []
  (drop 1 (get-list "sel_gur")))

(defn get-instructors []
  (drop 1 (get-list "sel_inst")))

(def form-url "https://admin.wwu.edu/pls/wwis/wwsktime.ListClass")

(def default-params
  {:content-type "text/html; charset=UTF-8"
   :form-params
   {:sel_gur  "All"
    :sel_subj "All"
    :sel_inst "ANY"
    :sel_crse ""
    :begin_hh  0
    :begin_mi "A"
    :end_hh    0
    :end_mi   "A"
    :sel_cdts "%"
    :sel_day  "dummy"
    :sel_open "dummy"
    :sel_crn  ""}})

(defn find-classes [{{:keys [sel_subj sel_inst sel_gur]}
                     :form-params
                     :as opts}]
  (if (or sel_subj sel_inst sel_gur)
    (let [opts (merge-with merge default-params opts)
          response-resource
          (-> form-url
              (client/post opts)
              :body
              html/html-snippet)]
      (if-not (html/select response-resource
                             [:h3 :b #(html/content %)])
        (html/select response-resource
                     [:tr])
        (do
          (println "Got a \"No classes found\" message.")
          response-resource)))
    (println "Didn't have the right stuff!")))
