(ns myclojuretest.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [cognitect.transit :as transit]
            [compojure.core :refer [defroutes ANY]]
            [compojure.route :refer [resources]])
  (:import (java.io ByteArrayOutputStream)))

;; data model
(defonce mylists (atom {:flo [{:url "http://news.ycombinator.com" :title "HNews"}
                              {:url "http://google.com" :title "Google"}]
                        :test [{:url "http://lemonde.fr" :titel "LeMonde"}]
                        }))

(defn get-list [id]
  (get @mylists id []))

(defn all-lists []
  (keys @mylists))

(defn add-to-list! [id content]
  (swap! mylists update id conj content))

(defn transit-str [obj]
  (let [buffer (ByteArrayOutputStream. 4096)
        writer (transit/writer buffer :json)]
    (transit/write writer obj)
    (.toString buffer)))

(defresource lists
             :available-media-types ["text/json"]
             :handle-ok (fn [_]
                          (transit-str (all-lists))))

(defresource list-view [id]
             :available-media-types ["text/plain" "text/json"]
             :allowed-methods [:post :get]
             :exists? (fn [ctx]
                        (if-let [target (get-list (keyword id))]
                          {::target target}))
             :post! (fn [ctx]
                      (let [body (slurp (get-in ctx [:request :body]))]
                        (add-to-list! id body)))
             :handle-ok (fn  [ctx]
                          (transit-str (get-in ctx [::target]))))

(defroutes  app
            (ANY "/l/:listid" [listid] (list-view listid))
            (ANY "/l/" [] lists)
            (resources "/")) ;; should actually redirects to index.html..



(def handler
  (-> app
      wrap-params))            ;; query encoded and req body params -> request map :params


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
