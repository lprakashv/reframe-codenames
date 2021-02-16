(defproject reframe-codenames "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript
                  "1.10.773"
                  :exclusions
                  [com.google.javascript/closure-compiler-unshaded
                   org.clojure/google-closure-library
                   org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs "2.11.7"]
                 [reagent "0.10.0"]
                 [re-frame "1.1.2"]
                 [day8.re-frame/tracing "0.6.0"]
                 [day8.re-frame/http-fx "0.2.2"]
                 [garden "1.3.10"]
                 [ns-tracker "0.4.0"]
                 [clj-kondo "RELEASE"]]

  :plugins
  [[lein-shadow "0.3.1"]
   [lein-garden "0.3.0"]
   [lein-shell "0.5.0"]]

  :min-lein-version "2.9.0"

  :source-paths ["src/clj" "src/cljs"]

  :clean-targets
  ^{:protect false} ["docs/js/compiled"
                     "target"
                     "docs/css"]


  :garden
  {:builds [{:id           "screen"
             :source-paths ["src/clj"]
             :stylesheet   reframe-codenames.css/screen
             :compiler     {:output-to     "docs/css/screen.css"
                            :pretty-print? true}}]}

  :shadow-cljs
  {:nrepl  {:port 8777}

   :builds {:app {:target     :browser
                  :output-dir "docs/js/compiled"
                  :asset-path "/js/compiled"
                  :modules    {:app {:init-fn  reframe-codenames.core/init
                                     :preloads [devtools.preload
                                                re-frisk.preload]}}
                  :dev        {:compiler-options {:closure-defines {re-frame.trace.trace-enabled?        true
                                                                    day8.re-frame.tracing.trace-enabled? true}}}
                  :release    {:build-options
                               {:ns-aliases
                                {day8.re-frame.tracing day8.re-frame.tracing-stubs}}}

                  :devtools   {:http-root "docs"
                               :http-port 8280}}}}

  :shell
  {:commands {"karma" {:windows         ["cmd" "/c" "karma"]
                       :default-command "karma"}
              "open"  {:windows ["cmd" "/c" "start"]
                       :macosx  "open"
                       :linux   "xdg-open"}}}

  :aliases
  {"dev"          ["do"
                   ["shell" "echo" "\"DEPRECATED: Please use lein watch instead.\""]
                   ["watch"]]
   "watch"        ["with-profile"
                   "dev"
                   "do"
                   ["shadow" "watch" "app" "browser-test" "karma-test"]]

   "prod"         ["do"
                   ["shell" "echo" "\"DEPRECATED: Please use lein release instead.\""]
                   ["release"]]

   "release"      ["with-profile"
                   "prod"
                   "do"
                   ["shadow" "release" "app"]]

   "build-report" ["with-profile"
                   "prod"
                   "do"
                   ["shadow" "run" "shadow.cljs.build-report" "app" "target/build-report.html"]
                   ["shell" "open" "target/build-report.html"]]

   "karma"        ["do"
                   ["shell" "echo" "\"DEPRECATED: Please use lein ci instead.\""]
                   ["ci"]]
   "ci"           ["with-profile"
                   "prod"
                   "do"
                   ["shadow" "compile" "karma-test"]
                   ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]
   "clj-kondo"    ["run" "-m" "clj-kondo.main" "--lint" "src"]}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "1.0.2"]
                   [re-frisk "1.3.4"]]
    :source-paths ["dev"]}

   :prod {}}

  :prep-tasks [["garden" "once"]])