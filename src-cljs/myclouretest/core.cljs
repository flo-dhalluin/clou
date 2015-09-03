 (ns myclouretest.core
   (:require-macros [cljs.core.async.macros :refer [go]])
  (:require[om.core :as om :include-macros true]
           [om.dom :as dom :include-macros true]
           [cognitect.transit :as t]
           [cljs-http.client :as http]
           [cljs.core.async :refer [put! chan <!]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defonce app-state
         (atom
          {:text "Hello world! Chombier"
           :items [{:url "http://www.google.com" :title "goog" :text "da search engine"}
                   {:url "http://www.reddit.com" :title "reddit" :text "the reddit"}]}))

(defn item-view [item owner]
      (reify
       om/IRender
       (render [this]
               (dom/div nil
                        (dom/h4 nil (:title item))
                        (dom/p nil (:text item))))))


(defn add-item [data owner]
      (let [new-item (-> (om/get-node owner "new-item")
                         .-value)]
           (when new-item
                 (om/transact! data :items #(conj % {:url new-item :title new-item :text "lenovo"})))))



(defn items-view [data owner]
      (reify
        om/IInitState
        (init-state [_]
          (prn "init state for items-view")
          (go (let [response (<! (http/get "/l/flo"))
                    r (t/reader :json)
                    parsed-list (t/read r (:body response))]
                (prn parsed-list)
                (om/transact! data :items #(parsed-list)))))

        om/IRender
        (render [this]
          (dom/div nil
                   (dom/h2 nil "List")
                   (apply dom/div nil
                          (om/build-all item-view (:items data)))
                   (dom/div nil
                            (dom/input #js {:type "text" :ref "new-item"})
                            (dom/button #js {:onClick #(add-item data owner)} "add"))))))

(om/root
 items-view
 app-state
 {:target (. js/document (getElementById "items"))})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
