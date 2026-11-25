import React from "react";
import { AbsoluteFill, interpolate, Sequence, staticFile, useVideoConfig } from "remotion";
import { Audio } from "@remotion/media";
import { loadFont } from "@remotion/fonts";
import { BANG, HAND, PAPER } from "./kit";
import { HeightScene, IntroScene } from "./scenes1";
import { AlbumScene, SpicyScene } from "./scenes2";
import { DnaScene } from "./scenes3";
import { HeartScene, OutroScene, ThanksgivingScene } from "./scenes4";

loadFont({ family: HAND, url: staticFile("fonts/PatrickHand.woff2") });
loadFont({ family: BANG, url: staticFile("fonts/Bangers.woff2") });

// Scene lengths in seconds (total = 120s)
export const SCENES = {
  intro: 9,
  height: 21,
  album: 13.333333333333334,
  spicy: 6.666666666666667,
  dna: 22,
  thanksgiving: 24,
  heart: 14,
  outro: 10,
};

export const AdoptedStory: React.FC = () => {
  const { fps } = useVideoConfig();
  let t = 0;
  const at = (sec: number) => {
    const from = Math.round(t * fps);
    t += sec;
    return { from, durationInFrames: Math.round(sec * fps) };
  };
  const intro = at(SCENES.intro);
  const height = at(SCENES.height);
  const album = at(SCENES.album);
  const spicy = at(SCENES.spicy);
  const dna = at(SCENES.dna);
  const thanks = at(SCENES.thanksgiving);
  const heart = at(SCENES.heart);
  const outro = at(SCENES.outro);

  // Music gets quieter under the big reveals and stops for the heartfelt part.
  const resultsStart = dna.from + 360;
  const musicVolume = (f: number) =>
    interpolate(
      f,
      [0, resultsStart - 10, resultsStart, dna.from + dna.durationInFrames, thanks.from + 128, thanks.from + 136, thanks.from + 250, heart.from - 15, heart.from, outro.from, outro.from + 15, 3590, 3600],
      [0.22, 0.22, 0.04, 0.12, 0.2, 0.03, 0.18, 0.18, 0, 0, 0.22, 0.22, 0],
      { extrapolateLeft: "clamp", extrapolateRight: "clamp" },
    );

  return (
    <AbsoluteFill style={{ backgroundColor: PAPER }}>
      <Audio src={staticFile("audio/music.wav")} volume={musicVolume} />
      <Sequence {...heart} name="Soft music">
        <Audio src={staticFile("audio/music-soft.wav")} volume={0.45} />
      </Sequence>
      <Sequence {...intro} name="1 Intro">
        <IntroScene />
      </Sequence>
      <Sequence {...height} name="2 Height">
        <HeightScene />
      </Sequence>
      <Sequence {...album} name="3 Baby pictures">
        <AlbumScene />
      </Sequence>
      <Sequence {...spicy} name="4 Spicy food">
        <SpicyScene />
      </Sequence>
      <Sequence {...dna} name="5 DNA test">
        <DnaScene />
      </Sequence>
      <Sequence {...thanks} name="6 Thanksgiving">
        <ThanksgivingScene />
      </Sequence>
      <Sequence {...heart} name="7 We picked you">
        <HeartScene />
      </Sequence>
      <Sequence {...outro} name="8 Outro">
        <OutroScene />
      </Sequence>
    </AbsoluteFill>
  );
};
