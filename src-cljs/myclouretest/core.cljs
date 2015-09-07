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
           {:current-list ""
            :all-lists [:blah :chombier :nonnnn]
            :items []}))

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

(defn lists-view [data owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (go (let [resp (<! (http/get "l/"))
                r (t/reader :json)
                llists (t/read r (:body resp))]
            (om/update! data :all-lists (vec llists)))))
    om/IRender
    (render [this]
      (dom/div #js{:class "list-view"}
               (apply dom/ul nil
                      (map (fn [list-name]
                             (dom/li nil (str list-name))) (:all-lists data))) ))))

(defn items-view [data owner {:keys [list-name] :as opts}]
      (reify
        om/IWillMount
        (will-mount [_]
          (prn "init state for items-view")
          (go (let [url (clojure.string/join ["/l/" list-name])
                    response (<! (http/get url))
                    r (t/reader :json)
                    parsed-list (t/read r (:body response))]
                (prn parsed-list)
                (om/update! data :items (vec parsed-list)))))

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
  lists-view
  app-state
  {:target (. js/document (getElementById "lists"))})

(om/root
 items-view
 app-state
 {:target (. js/document (getElementById "items"))
  :opts {:list-name "flo"}})


(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
