(ns myclojuretest.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [cognitect.transit :as transit]
            [compojure.core :refer [defroutes ANY]]
            [compojure.route :refer [resources]])
  (:import (java.io ByteArrayOutputStream)))

;; data model
(def mylists (atom {:ab ["blah", "chobmier", "bale"]
              :flo ["one", "two", "three"] }))


(defn get-list [id]
  (get @mylists id))


(defn add-to-list [id content]
  (swap! mylists update id #(concat % [content])))


(defresource list [id]
             :available-media-types ["text/plain"]
             :allowed-methods [:post :get]
             :exists? (fn [ctx]
                        (if-let [target (get-list (keyword id))]
                          {::target target}))
             :post! (fn [ctx]
                      (let [body (slurp (get-in ctx [:request :body]))]
                        (add-to-list id body)))
             :handle-ok (fn  [ctx]
                          (let [buffer (ByteArrayOutputStream. 4096)
                                writer (transit/writer buffer :json)]
                            (transit/write writer (get-in ctx [::target]))
                            (.toString buffer))))

(defroutes  app
            (ANY "/l/:listid" [listid] (list listid)))
            ;;(resources "/"))



(def handler
  (-> app
      wrap-params))            ;; query encoded and req body params -> request map :params


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
