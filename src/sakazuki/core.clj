(ns sakazuki.core
  (:require [scad-clj.scad :as scad]
            [scad-clj.model :as model]
            [clojure.core.matrix :as matrix]))

; (defn -main []
;   (println "done"))

(def radius 200)
(def angle (* Math/PI 0.032))

(def single 18.5)
(def double-plate 37.5)

; (def single-angle (* (Math/atan2 (/ single 2) 200) 2))
; (def gap-angle (- angle single-angle))

; (def plate-radius (Math/sqrt (- (Math/pow radius 2) (Math/pow (/ single 2) 2))))

(def keyswitch-width 14.4)
(def keyswitch-height 14.4)
; (def sa-profile-key-height 12.7)
; (def mount-width (+ keyswitch-width 3))
; (def mount-height (+ keyswitch-height 3))

(def plate-depth 4)
; (def switch-depth 5)
(def wall-width (/ (- single keyswitch-width) 2))

(def plate
  (let [wall-height (+ keyswitch-width (* wall-width 2))
        wall (->> (scad-clj.model/cube wall-width wall-height plate-depth)
                  (model/translate [(+ (/ wall-width 2) (/ keyswitch-width 2))
                                    0
                                    (/ plate-depth 2)]))
        nub-radius 1
        nub-height 2.75
        nub-body (->> (model/cube wall-width nub-height plate-depth)
                      (model/translate [(/ wall-width 2) 0 (/ plate-depth 2)]))
        nub-foot (->> (binding [model/*fn* 100] (model/cylinder nub-radius nub-height))
                      (model/rotate (/ Math/PI 2) [1 0 0])
                      (model/translate [0 0 nub-radius]))
        nub (->> nub-body
                 (model/hull nub-foot)
                 (model/translate [(- (/ wall-height 2) wall-width) 0 0]))]
    (model/union (map
                  (fn [i] (->> wall
                               (model/rotate (* (/ Math/PI 2) i) [0 0 1])))
                  (range 4))
                 (map
                  (fn
                    [i]
                    (->> nub
                         (model/rotate (* Math/PI i) [0 0 1])))
                  (range 2)))))

; (defn spread [shape col row]
;   (let [ox (/ (- col 1) -2)
;         oy (/ (- row 1) -2)]
;     (union (map
;             (fn [x]
;               (map
;                (fn [y]
;                  (->> shape
;                       (translate [0 0 (- plate-radius)])
;                       (rotate (* angle (+ oy y)) [1 0 0])
;                       (rotate (* angle (+ ox x)) [0 1 0])
;                       (translate [0 0 plate-radius])))
;                (range row)))
;             (range col)))))

; (defn connect [col row]
;   (let [shape (->> (cube (- (* radius angle) single) single plate-depth)
;                    (translate [0 0 (/ plate-depth 2)]))
;         ox (+ (/ col -2) 1)
;         oy (+ (/ row -2) 0.5)]
;     (union
;      (map
;       (fn [x]
;         (map
;          (fn [y]
;            (->> shape
;                 (translate [0 0 (- radius)])
;                 (rotate (* angle (+ oy y)) [1 0 0])
;                 (rotate (* angle (+ ox x)) [0 1 0])
;                 (translate [0 0 radius])))
;          (range row)))
;       (range (- col 1))))))

(def sa-cap-1
  (let [bl2 (/ single 2)
        m (/ 17 2)
        key-cap (model/hull (->> (model/polygon [[bl2 bl2] [bl2 (- bl2)] [(- bl2) (- bl2)] [(- bl2) bl2]])
                                 (model/extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                 (model/translate [0 0 0.05]))
                            (->> (model/polygon [[m m] [m (- m)] [(- m) (- m)] [(- m) m]])
                                 (model/extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                 (model/translate [0 0 6]))
                            (->> (model/polygon [[6 6] [6 -6] [-6 -6] [-6 6]])
                                 (model/extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                 (model/translate [0 0 12])))]
    (->> key-cap
         (model/color [220/255 163/255 163/255 1]))))

(def sa-cap-2
  (let [bl2 (/ double-plate 2)
        bw2 (/ single 2)
        key-cap (model/hull (->> (model/polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                 (model/extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                 (model/translate [0 0 0.05]))
                            (->> (model/polygon [[6 16] [6 -16] [-6 -16] [-6 16]])
                                 (model/extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                 (model/translate [0 0 12])))]
    (->> key-cap
         (model/color [127/255 159/255 127/255 1]))))

(def sa-cap-1-5
  (let [bl2 (/ single 2)
        bw2 (/ 28 2)
        key-cap (model/hull (->> (model/polygon [[bw2 bl2] [bw2 (- bl2)] [(- bw2) (- bl2)] [(- bw2) bl2]])
                                 (model/extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                 (model/translate [0 0 0.05]))
                            (->> (model/polygon [[11 6] [-11 6] [-11 -6] [11 -6]])
                                 (model/extrude-linear {:height 0.1 :twist 0 :convexity 0})
                                 (model/translate [0 0 12])))]
    (->> key-cap
         (model/color [240/255 223/255 175/255 1]))))

(def sa-cap
  (model/union
   (->> sa-cap-1
        (model/translate [-30 0 0]))
   (->> sa-cap-2
        (model/translate [0 0 0]))
   (->> sa-cap-1-5
        (model/translate [30 0 0]))))

(def main-col 6)
(def main-row 3)
(def sub-col 4)
(def sub-row 1)
; (defn bottom-size [n]
;   (* radius (Math/sin (+ (* angle (/ n 2)) (* gap-angle (/ (- n 1) 2))))))

; (def key
;   (union
;    plate
;    (->> sa-cap-1
;         (translate [0 0 (+ plate-depth switch-depth)]))))

(defn holes [col row w h]
  (let [shape (model/cube w h radius)
        ox (/ (- col 1) -2)
        oy (/ (- row 1) -2)]
    (model/union
     (map
      (fn [x]
        (map
         (fn [y]
           (->> shape
                (model/translate [0 0 (* radius -0.5)])
                (model/rotate (* angle (+ oy y)) [1 0 0])
                (model/rotate (* angle (+ ox x)) [0 1 0])
                (model/translate [0 0 radius])))
         (range row)))
      (range col)))))

(defn suf [col row]
  (model/intersection
   (->> (model/difference
         (binding [model/*fn* 100] (model/sphere radius))
         (binding [model/*fn* 100] (model/sphere (- radius plate-depth))))
        (model/translate [0 0 radius]))
   (->> (holes col row single single)
        model/hull)))

(def nub2
  (let [nub-radius 1
        nub-height 2.75
        nub-body (->> (model/cube wall-width nub-height plate-depth)
                      (model/translate [(/ wall-width 2) 0 (/ plate-depth 2)]))
        nub-foot (->> (binding [model/*fn* 100] (model/cylinder nub-radius nub-height))
                      (model/rotate (/ Math/PI 2) [1 0 0])
                      (model/translate [0 0 nub-radius]))
        nub-one (->> nub-body
                     (model/hull nub-foot)
                     #_(model/translate [(- (/ single 2) wall-width) 0 0])
                     (model/color [1 0 0 1]))]
    nub-one))

(defn spread2 [shape col row dx dy]
  (let [ox (/ (- col 1) -2)
        oy (/ (- row 1) -2)]
    (model/union
     (map
      (fn [x]
        (map
         (fn [y]
           (->> shape
                (model/translate [0 0 (- radius)])
                (model/rotate (+ (* angle (+ oy y)) dx) [1 0 0])
                (model/rotate (+ (* angle (+ ox x)) dy) [0 1 0])
                (model/translate [0 0 radius])))
         (range row)))
      (range col)))))

(defn legs [col row]
  (let [shape (->> (model/cube wall-width wall-width 30)
                   (model/color [0 1 0 1]))
        ox (/ (- col 1) -2)
        oy (/ (- row 1) -2)]
    (model/union
     (->> shape
          (model/translate [(/ (+ keyswitch-width wall-width) 2) (/ (+ keyswitch-height wall-width) -2) (- radius)])
          (model/rotate (* angle (+ oy 0)) [1 0 0])
          (model/rotate (* angle (+ ox 0)) [0 1 0])
          (model/translate [0 0 radius])))))

(defn plate2 [col row]
  (model/union
   (model/difference
    (suf col row)
    (holes col row keyswitch-width keyswitch-height))
   (spread2 nub2 col row 0 (Math/atan2 (/ keyswitch-width -2) radius))
   (spread2 (->> nub2 (model/rotate Math/PI [0 0 1])) col row 0 (Math/atan2 (/ keyswitch-width 2) radius))))

(def main
  (let [pl (plate2 main-col main-row)]
    (model/union
     pl
     (legs main-col main-row))))

(def top-plates
  (model/union
   (->> main
        (model/rotate (* Math/PI 0.05) [1 0 0])
        (model/translate [0 0 15]))
   (->> (plate2 sub-col sub-row)
        (model/rotate (* Math/PI 0.18) [0.9 0.4 0])
        (model/translate [-50 -39 11]))))

(defn mat []
  (let [ox (/ (- main-col 1) -2)
        oy (/ (- main-row 1) -2)]
    (matrix/add [[1 0 0 0]
                 [0 1 0 0]
                 [0 0 1 radius]
                 [0 0 0 1]]
                (matrix/mul [[(* angle (+ oy 0)) 0 0 0]
                             [0 (* angle (+ ox 0)) 0 0]
                             [0 0 1 0]
                             [0 0 0 1]]
                            [[1 0 0 (/ (+ keyswitch-width wall-width) 2)]
                             [0 1 0 (/ (+ keyswitch-height wall-width) -2)]
                             [0 0 1 (- radius)]
                             [0 0 0 1]]))))
(matrix/pm (mat))
(println (mat))
(println (vec (flatten (mat))))

(def top
  (model/union
   top-plates
   (->> (model/sphere 5)
        (model/multmatrix (mat))
        ; (model/multmatrix [[1 0 0 100] [0 1 0 100] [0 0 1 100] [0 0 0 1]])
        )))

(def whole
  (model/union
   top))

(spit "hull.scad"
      (scad/write-scad (model/union
                        (->> (model/cube 10 20 0.0001)
                             #_(model/rotate (/ Math/PI 3) [1 0 0]))
                        (->> (model/cube 10 20 0.0001)
                             (model/translate [0 0 30])
                             #_(model/rotate (/ Math/PI -3) [1 0 0])))))

(spit "sa-cap.scad"
      (scad/write-scad sa-cap))
(spit "plate.scad"
      (scad/write-scad plate))
(spit "sakazuki.scad"
      (scad/write-scad whole))
