import { Composition } from "remotion";
import { AdoptedStory } from "./story/AdoptedStory";

export const RemotionRoot: React.FC = () => {
  return (
    <>
      <Composition
        id="AdoptedStoryTime"
        component={AdoptedStory}
        durationInFrames={3600}
        fps={30}
        width={1920}
        height={1080}
      />
    </>
  );
};
