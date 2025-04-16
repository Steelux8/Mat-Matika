package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.bar.PortsideBarEvent;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventCreator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;

public class TestBarEventCreator extends BaseBarEventCreator {

    public PortsideBarEvent createBarEvent() {
        return new TestBarEvent();
    }

    @Override
    public float getBarEventAcceptedTimeoutDuration() {
        return 10000000000f; // one-time-only
    }

    @Override
    public float getBarEventFrequencyWeight() {
        if (!Global.getSector().getMemoryWithoutUpdate().contains(MiscellaneousThemeGenerator.PLANETARY_SHIELD_PLANET_KEY)) {
            return 0f;
        }
        return 1000000f;
    }
    @Override
    public boolean isPriority() {
        return true;
    }
}
