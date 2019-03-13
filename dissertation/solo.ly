\version "2.10.0"
\header {
	title = "JazzGen Composition"
	date = "April 25, 2008"
}

\include "english.ly"

PianoSolo = {
  d''16 d''16 c''16 a'16 d''16 c''16 as'16 c''16 d''16 e''16 a'16 c''16 d''16 g'16 d'16 a16 d'16 a'16 as'16 c''8 d''8 e''16 a'16 g'16 e'16 d'16 c'16 d'16 e'16 g'8 a'8 g'16 f'16 g''16 d''16 a'16 g'8 f'4 g'2~g'8. as'8 c''16 d''4 e''4 f''8 f''8 g''16 a''8 a'8 a'8 g'16 g'4.. a'8 c''8 d''8. a''4 as''4 c'''16 d'''2 d'''2 e'8 g'8 a'8. 
}
PianoChords = {
  \clef bass
  <c, e, g, as, >1~<c, e, g, as, >1 <f, a, c, ds, >1 <c e, g, as, >1 <g, b, d, f, >1 <f, a, c, ds, >1 <c, e, g, as, >1~<c, e, g, as, >1 
}
Bass = {
  c,4 c,4 e,4 g,4 as,4 as,4 g,4 e,4 f,4 a,4 f,4 f,4 c,4 c4 e,4 g,4 g,4 b,4 d,4 b,4 f,4 a,4 c,4 ds,4 c,4 c,4 e,4 g,4 e,4 g,4 as,4 g,4
}
DrumsSNBD = \drummode {
  \times 2/3 {bd4 r8} \times 2/3 {r4 sn8}
}
DrumsHH = \drummode {
  hh4 \times 2/3 {hh4 hh8} hh4 hh4
}

piano = {
  <<
    \set PianoStaff.instrumentName = #"Piano "
    \new Staff = "upper" \PianoSolo
    \new Staff = "lower" \PianoChords
  >>
}
bass = {
  \set Staff.instrumentName = #"Bass   "
  \clef bass
  <<
    \Bass
  >>
}
hihat = {
  <<
    \set DrumStaff.instrumentName = #"Drums   "
    \new DrumVoice { \DrumsHH \DrumsHH \DrumsHH \DrumsHH \DrumsHH \DrumsHH \DrumsHH \DrumsHH  }
  >>
}
snbd = {
  <<
    \new DrumVoice { \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD \DrumsSNBD }
  >>
}

\score {
  <<
    \override Score.MetronomeMark #'padding = #3
    \tempo 4 = 120

    \new PianoStaff = "piano" \piano
    \new Staff = "bass" \bass
    \new DrumStaff { \hihat }
    \new DrumStaff { \snbd }
  >>
}

\midi { }
