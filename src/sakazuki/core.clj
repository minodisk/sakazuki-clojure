(ns sakazuki.core
  (:require [scad-clj.scad :refer :all]
            [scad-clj.model :refer :all]))

(defn -main []
  (println "done"))

(def sa-length 18.5)
(def sa-double-length 37.5)

(def keyswitch-width 14.4)
(def keyswitch-height 14.4)
; (def sa-profile-key-height 12.7)
; (def mount-width (+ keyswitch-width 3))
; (def mount-height (+ keyswitch-height 3))

(def wall-depth 4)
(def switch-depth 5)

(def plate
  (let [wall-width (/ (- sa-length keyswitch-width) 2)
        wall-height (+ keyswitch-width (* wall-width 2))
        wall (->> (cube wall-width wall-height wall-depth)
                  (translate [(+ (/ wall-width 2) (/ keyswitch-width 2))
                              0
                              (/ wall-depth 2)]))
        nub-radius 1
        nub-height 2.75
        nub-body (->> (cube wall-width nub-height wall-depth)
                      (translate [(/ wall-width 2) 0 (/ wall-depth 2)]))
        nub-foot (->> (binding [*fn* 100] (cylinder nub-radius nub-height))
                      (rotate (/ Math/PI 2) [1 0 0])
                      (translate [0 0 nub-radius]))
        nub (->> nub-body
                 (hull nub-foot)
                 (translate [(- (/ wall-height 2) wall-width) 0 0]))]
    (union (map
            (fn [i] (->> wall
                         (rotate (* (/ Math/PI 2) i) [0 0 1])))
            (range 4))
           (map
            (fn
              [i]
              (->> nub
                   (rotate (* Math/PI i) [0 0 1])))
            (range 2)))))

(defn spread [shape col row]
  (let [angle (* Math/PI 0.032)
        ox (/ (- col 1) -2)
        oy (/ (- row 1) -2)]
    (union (map
            (fn [x]
              (map
               (fn [y]
                 (->> shape
                      (translate [0 0 -200])
                      (rotate (* angle (+ oy y)) [1 0 0])
                      (rotate (* angle (+ ox x)) [0 1 0])
                      (translate [0 0 200])))
               (range row)))
            (range col)))))

(defn connect [col row]
  (let []
    (->> (circle 100)
         (extrude-linear 5))))

(def sa-cap-1
  (let [bl2 (/ sa-length 2)
        m (/ 17 2)
        key-cap (hull (->> (polygon [[bl2 bl2] [bl2 (- bl2)] [(- bl2) (- bl2)] [(- bl2) bl2]])
                           (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                           (translate [0 0 0.05]))
                      (->> (polygon [[m m] [m (- m)] [(- m) (- m)] [(- m) m]])
                           (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                           (translate [0 0 6]))
                      (->> (polygon [[6 6] [6 -6] [-6 -6] [-6 6]])
                           (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                           (translate [0 0 12])))]
    (->> key-cap
         (color [220/255 163/255 163/255 1]))))

(def sa-cap-2
  (let [bl2 (/ sa-double-length 2)
        bw2 (/ sa-length 2)
        key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                           (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                           (translate [0 0 0.05]))
                      (->> (polygon [[6 16] [6 -16] [-6 -16] [-6 16]])
                           (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                           (translate [0 0 12])))]
    (->> key-cap
         (color [127/255 159/255 127/255 1]))))

(def sa-cap-1-5
  (let [bl2 (/ sa-length 2)
        bw2 (/ 28 2)
        key-cap (hull (->> (polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                           (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                           (translate [0 0 0.05]))
                      (->> (polygon [[11 6] [-11 6] [-11 -6] [11 -6]])
                           (extrude-linear {:height 0.1 :twist 0 :convexity 0})
                           (translate [0 0 12])))]
    (->> key-cap
         (color [240/255 223/255 175/255 1]))))

(def sa-cap
  (union
   (->> sa-cap-1
        (translate [-30 0 0]))
   (->> sa-cap-2
        (translate [0 0 0]))
   (->> sa-cap-1-5
        (translate [30 0 0]))))

(def key
  (union
   plate
   (->> sa-cap-1
        (translate [0 0 (+ wall-depth switch-depth)]))))
(def keys
  (union
   (->> (spread key 6 3)
        (rotate (* Math/PI 0.1) [1 0 0])
        (rotate (* Math/PI 0.05) [0 1 0])
        (translate [0 0 20]))
   (connect 6 3)
   (->> (spread key 4 1)
        (rotate (* Math/PI 0.2) [1 0 0])
        (rotate (* Math/PI 0.05) [0 1 0])
        (translate [-50 -35 15]))))

(def top
  keys)

(def whole
  (->> top))

(spit "sa-cap.scad"
      (write-scad sa-cap))
(spit "plate.scad"
      (write-scad plate))
(spit "sakazuki.scad"
      (write-scad whole))
