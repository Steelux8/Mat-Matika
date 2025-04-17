package src.author.modname;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import data.scripts.TestBarEventCreator;

public class MatMatikaPlugin extends BaseModPlugin {

    @Override
    public void onGameLoad(boolean newGame) {
        BarEventManager bar = BarEventManager.getInstance();
        bar.addEventCreator(new TestBarEventCreator());
    }

}
