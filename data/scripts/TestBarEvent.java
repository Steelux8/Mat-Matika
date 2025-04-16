package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventWithPerson;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.awt.*;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class TestBarEvent extends BaseBarEventWithPerson {
    public enum OptionId {
        INIT_1, INIT_2, INSIST, CONTINUE_1, CONTINUE_2, WHERE_WAS_SYSTEM, CONTINUE_3, LEAVE
    }
    @Override
    public boolean isAlwaysShow() {
        return true;
    }

    public static PlanetAPI getTargetPlanet() {
        return (PlanetAPI) Global.getSector().getMemoryWithoutUpdate().get(MiscellaneousThemeGenerator.PLANETARY_SHIELD_PLANET_KEY);
    }


    public TestBarEvent() {
        super();
    }

    public boolean shouldShowAtMarket(MarketAPI market) {
        if (!super.shouldShowAtMarket(market)) return false;

        return market.getFactionId().equals(Factions.HEGEMONY);
    }

    protected PersonAPI pilot;
    protected MarketAPI pilotMarket = null;
    @Override
    protected void regen(MarketAPI market) {
        if (this.market == market) return;
        super.regen(market);

        if (person.getGender() == FullName.Gender.MALE) {
            person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "old_spacer_male"));
        } else {
            person.setPortraitSprite(Global.getSettings().getSpriteName("intel", "old_spacer_female"));
        }

        pilot = Global.getSector().getFaction(Factions.INDEPENDENT).createRandomPerson(random);
        pilot.setRankId(Ranks.PILOT);
        pilot.setPostId(Ranks.POST_CITIZEN);

        WeightedRandomPicker<MarketAPI> picker = new WeightedRandomPicker<MarketAPI>(random);
        for (MarketAPI curr : Global.getSector().getEconomy().getMarketsInGroup(null)) {
            if (curr == market) continue;
            if (curr.isPlayerOwned()) continue;
            if (curr.isHidden()) continue;
            if (curr.isInvalidMissionTarget()) continue;
            if (curr.getStabilityValue() <= 0) continue;

            float w = curr.getSize();
            if (curr.isFreePort()) w += 10f;
            picker.add(curr, w);
        }

        if (picker.isEmpty()) picker.add(market, 1f);

        pilotMarket = picker.pick();
    }

    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.addPromptAndOption(dialog, memoryMap);

        regen(dialog.getInteractionTarget().getMarket());

        TextPanelAPI text = dialog.getTextPanel();
        text.addPara("A disgruntled mathematician slams the table he's working on." +
                "He mutters a few words of frustration upon the papers surrounding him.");

		Color c = Misc.getHighlightColor();
		c = Misc.getHighlightedOptionColor();

        dialog.getOptionPanel().addOption("Approach the mathematician and find out what's troubling him", this,
                null);
    }


    @Override
    public void init(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.init(dialog, memoryMap);

        done = false;

        dialog.getVisualPanel().showPersonInfo(person, true);

        optionSelected(null, TestBarEvent.OptionId.INIT_1);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        if (!(optionData instanceof TestBarEvent.OptionId)) {
            return;
        }
        TestBarEvent.OptionId option = (TestBarEvent.OptionId) optionData;

        OptionPanelAPI options = dialog.getOptionPanel();
        TextPanelAPI text = dialog.getTextPanel();
        options.clearOptions();

//		continue
//		> Offer to apologize for the misunderstanding by buying $himOrHer a drink.
//
//		exit
//		> Suggest that they read the manual then leave.

        switch (option) {
            case INIT_1:
                text.addPara(
                        getHeOrShe() + " turns to you with distraught eyes, before reality rushes back." +
                        " \"Ah, what is it? I am quite busy right now, getting nowhere.\" " + getHeOrShe() +
                        " drops " + getHisOrHer() + " antique pen on the papers, defeated." +
                        " \"Between all these equations and formulas, I sometimes think we're all in way above our heads. " +
                        "Don't tell anyone I said this, though. I should get going."
                );
                options.addOption("Ask what the problem is", OptionId.INIT_2);
                break;
            case INIT_2:
                text.addPara(
                        "\"Oh, you're interested? Very good, curiosity is mankind's greatest tool.\" " +
                        getHeOrShe() + " flashes a kind smile, but nods to the sides," +
                        " \"Unfortunately, curiosity alone will not get us far anymore. The basic secrets of the" +
                        " universe have already been revealed, and all that is left are conundrums of the greatest order." +
                        " I'm afraid you won't be able to help me with them."
                );
                options.addOption("Insist that you could be of some help", OptionId.INSIST);
                options.addOption("Nod in sympathy and walk away from the formulas", TestBarEvent.OptionId.LEAVE);
            case INSIST:
                text.addPara(
                        "The mathematician examines you more closely, furrowing " + getHisOrHer() + "brow." +
                        " \"You are a fleet captain, are you not? Hmm...\" " +
                        "\n\n" + getHeOrShe() + " glances towards the papers scattered on the table for a while, " +
                        "before returning to you. \"If you are who I've heard you are, perhaps you could help me. " +
                        "You see, our budget for many of our issues is quite limited as it is, but then we also " +
                        "have our fair share of... obstacles, as do the other factions. No matter how much we " +
                        "organize ourselves, others will come to ruin everything, like those pirates.\"\n\n" +
                        getHeOrShe() + " looks towards a fellow sitting on the opposite end of the bar. " +
                        getHisOrHer() + " frustration is palpable."
                );
                options.addOption("\"Do you need help dealing with pirates?\"", TestBarEvent.OptionId.CONTINUE_1);
                break;
            case CONTINUE_1:
                text.addPara("There they found a planet all shining and red. \"It weren't anything natural. " +
                        "It was all... shapes, angles, glowing like plasma. That's truth,\" " + getHeOrShe() +
                        " says quietly, \"I've seen Gates, sure, and orbital works big as you like. Ain't " +
                        "never seen anything Domain-made glow like that across a whole planet's face. " +
                        "Not anything that weren't a weapon, I mean.\"");

                options.addOption("Continue", TestBarEvent.OptionId.CONTINUE_2);
                break;
            case CONTINUE_2:
                text.addPara("\"While we were gawkin', the prox alarm goes and it's battle stations\", " + getHeOrShe() +
                        " continues. Hostile ships came fast upon the salvage fleet, flitting with agility " +
                        "belying advanced tech. They had equally advanced weapons, too, and with those they " +
                        "attacked with no mercy. \"It weren't pirates, nor military,\" here the spacer loses " +
                        getHisOrHer() + " cheer at a good story. \"Ludd's hells, I swear to you it weren't anything human.\"");

                text.addPara("The spacer explains that just " + getHeOrShe() + " and the pilot got away in an " +
                        "escape pod which was only \"mostly\" malfunctioning. \"The miracle wasn't that I " +
                        "fixed it, it's that they could thaw enough of me out at the end of it for me to keep " +
                        "livin', if you call this livin'.\"");

                options.addOption("\"Where was this system with the red planet?\"", TestBarEvent.OptionId.WHERE_WAS_SYSTEM);
                break;
            case WHERE_WAS_SYSTEM:
                text.addPara("You emphasize your interest in the subject by having the spacer's drink refreshed. " +
                        Misc.ucFirst(getHeOrShe()) + " shakes " + getHisOrHer() + " head, \"Captain, " +
                        "I wouldn't wish my fate on you or anyone. Besides, I have no idea.\" A pause, then, " +
                        "\"" + pilot.getNameString() + " would - that's the pilot,\" " + getHeOrShe() + " finally admits.");

                text.addPara("The old spacer tells you that " + pilot.getName().getFirst() + " did not live the experience in such " +
                        "stride as " + getHimOrHerself() + ", and has taken to drinking themselves senseless " +
                        "in semi-retirement " + pilotMarket.getOnOrAt() + " " + pilotMarket.getName() + ". " +
                        "\"Some folks, I don't think they react well " +
                        "to the emergency cryo-pods. Like a bit o' their brain is still froze up and not coming back.\"");

                String icon = Global.getSettings().getSpriteName("intel", "red_planet");
                Set<String> tags = new LinkedHashSet<String>();
                tags.add(Tags.INTEL_MISSIONS);

                dialog.getVisualPanel().showMapMarker(pilotMarket.getPrimaryEntity(),
                        "Destination: " + pilotMarket.getName(), pilotMarket.getFaction().getBaseUIColor(),
                        true, icon, null, tags);

                options.addOption("Continue", TestBarEvent.OptionId.CONTINUE_3);
                break;
            case CONTINUE_3:
                text.addPara("You consider a trip to " + pilotMarket.getName() + " to see if you can get the exact location of " +
                        "this mysterious planet with its unknown technology. You also realize that the old spacer " +
                        "has fallen asleep in " + getHisOrHer() + " seat, cybernetic eyes blanked out in standby mode.");

                BarEventManager.getInstance().notifyWasInteractedWith(this);
                addIntel();

                options.addOption("Leave the old spacer to " + getHisOrHer() + " rest", TestBarEvent.OptionId.LEAVE);
                break;
            case LEAVE:
                noContinue = true;
                done = true;
                break;
        }
    }


    protected void addIntel() {
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        TextPanelAPI text = dialog.getTextPanel();

        PlanetAPI planet = getTargetPlanet();
        boolean success = false;
        if (planet != null) {
            TestIntel intel = new TestIntel(planet, this);
            if (!intel.isDone()) {
                Global.getSector().getIntelManager().addIntel(intel, false, text);
                success = true;
            }
        }

        if (!success) {
            text.addPara("For a minute there, you were caught by the story, but you now see that following up " +
                    "on it would be a fool's errand.");
        }
    }

    @Override
    protected String getPersonFaction() {
        return Factions.INDEPENDENT;
    }

    @Override
    protected String getPersonRank() {
        return Ranks.SPACE_SAILOR;
    }

    @Override
    protected String getPersonPost() {
        return Ranks.CITIZEN;
    }

    @Override
    protected String getPersonPortrait() {
        return null;
    }

    @Override
    protected FullName.Gender getPersonGender() {
        return FullName.Gender.ANY;
    }

    public PersonAPI getPilot() {
        return pilot;
    }

    public MarketAPI getPilotMarket() {
        return pilotMarket;
    }

}
