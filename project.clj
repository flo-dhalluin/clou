(defproject myclojuretest "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :plugins [[lein-ring "0.8.11"]
            [lein-cljsbuild "1.0.5"]
            [lein-figwheel "0.3.5"]
            [cider/cider-nrepl "0.9.1"]]

  :ring {:handler myclojuretest.core/handler
         :nrepl {:start? true :port 9874}}

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [liberator "0.13"]
                 [compojure "1.3.4"]
                 [org.clojure/clojurescript "0.0-3297"]
                 [org.omcljs/om "0.8.8"]
                 [com.cognitect/transit-cljs "0.8.225"]
                 [com.cognitect/transit-clj "0.8.281"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [ring/ring-core "1.2.1"]
                 [cljs-http "0.1.37"]]

  :cljsbuild {
              :builds [{:id "dev"
                        :source-paths ["src-cljs"]
                        :figwheel {:on-jsload "myclouretest.core/on-js-reload"}
                        :compiler {:main myclouretest.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/myclojuretest.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true
                                   :optimizations :none
                                   :source-map true}}]


              }

  :figwheel {
             ;; :css-difs > watch and push css
             :ring-handler myclojuretest.core/handler
             :nrepl-port 9787
             ;; once connected : open cljs repl with
             ;; (use 'figwheel-sidecar.repl-api) (cljs-repl)

             }
  )


