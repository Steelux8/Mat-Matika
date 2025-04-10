package data.scripts.world.systems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.LocationAPI;
import com.fs.starfarer.api.campaign.OrbitAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.terrain.StarCoronaTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.terrain.MagneticFieldTerrainPlugin.MagneticFieldParams;

public class mm_arista implements SectorGeneratorPlugin {

    public void generate(SectorAPI sector) {

        boolean presetConditions = true;

        StarSystemAPI system = sector.createStarSystem("Arista");
        LocationAPI hyper = Global.getSector().getHyperspace();

        system.setBackgroundTextureFilename("data/scripts/world/systems/periphery.jpg");

//Set star

        PlanetAPI star = system.initStar(	"mm_arista", 					// unique id for star
                StarTypes.BROWN_DWARF, 				// type planets.json
                450f,						// radius (in pixels at default zoom)
                270); 						// corona radius, from star edge
        system.setLightColor(new Color(189, 213 ,143)); 					// light color in entire system, affects all entities

// Set Corona
        SectorEntityToken arista_corona = system.addTerrain(Terrain.CORONA,
                new StarCoronaTerrainPlugin.CoronaParams(763, //Range
                        381,	//Extra range?
                        star,	//ORBITAL ENTITY
                        1f, 	//WindStrength;
                        0f, 	//flareProbability?;
                        1f)	//crLossMult;
        );

        PlanetAPI aristaP1 = system.addPlanet("arista_planet_1",		//id
                star,				//orbital target
                "Alculus", 			//name
                "gas_giant", 			//type
                180, 335, 4478, 215); 		//angle/size/distance/period (days) 4880
// Set Planet (moon)
        PlanetAPI alculusM1 = system.addPlanet("alculus_moon_1",		//id
                aristaP1,				//orbital target
                "Integra", 			//name
                "water", 			//type
                305, 60, 1172, 57); 		//angle/size/distance/period (days) 4880

// Set Planet (moon)
        PlanetAPI alculusM2 = system.addPlanet("alculus_moon_2",		//id
                aristaP1,				//orbital target
                "Difer", 			//name
                "barren_venuslike", 			//type
                233, 63, 787, 36); 		//angle/size/distance/period (days) 4880

        SectorEntityToken alculusM2_field = system.addTerrain(Terrain.MAGNETIC_FIELD,
                new MagneticFieldParams(200f, // terrain effect band width
                        160f, // terrain effect middle radius
                        alculusM2, // entity that it's around
                        60f, // visual band start
                        260f, // visual band end
                        new Color(50, 20, 100, 50), // base color
                        0.25f, // probability to spawn aurora sequence, checked once/day when no aurora in progress
                        new Color(90, 180, 140),
                        new Color(130, 145, 190),
                        new Color(165, 110, 225),
                        new Color(95, 55, 240),
                        new Color(45, 0, 250),
                        new Color(20, 0, 240),
                        new Color(10, 0, 150)));
// Set Planet
        PlanetAPI aristaP2 = system.addPlanet("arista_planet_2",		//id
                star,				//orbital target
                "Alger", 			//name
                "rocky_metallic",		//type
                43, 95, 1627, 59); 		//angle/size/distance/period (days) 4880
        aristaP2.setCustomDescriptionId("alger_planet");

//Add a comm relay

        SectorEntityToken relay = system.addCustomEntity(	"pulp_relay", // unique id
                "Affro Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay_makeshift", // type of object, defined in custom_entities.json
                "matmatika"); // faction
        relay.setCircularOrbitPointingDown(star, 70, 1954, 50);

// Set Asteroid Belt (INNER)
        system.addAsteroidBelt(		aristaP1,			//ORBITAL ENTITY
                25,			//Density
                1721,			//Distance (centre)
                72,			//Width
                77,			//Orbital Speed (low)
                155,			//Orbital Speed (high)
                Terrain.RING,
                null); 			//default name	null	OR	"inner"
//Undeylying ring (INNER) (visual only)
        system.addRingBand(		aristaP1,			//ORBITAL ENTITY
                "misc",			//visual cat (settings.json)
                "rings_dust0",	//effect
                256f,			//width
                2,			//which one (see img)
                Color.gray,
                256f,			//width
                1672,			//distance
                110f);			//orbital period

        system.addRingBand(		aristaP1,			//ORBITAL ENTITY
                "misc",			//visual cat (settings.json)
                "rings_ice0",	//effect
                256f,			//width
                0,			//which one (see img)
                Color.gray,
                256f,			//width
                1771,			//distance
                104f);			//orbital period

// Set Asteroid Belt (OUTER)
        system.addAsteroidBelt(		star,			//ORBITAL ENTITY
                25,			//Density
                6200,			//Distance (centre)
                72,			//Width
                77,			//Orbital Speed (low)
                155,			//Orbital Speed (high)
                Terrain.RING,
                null); 			//default name	null	OR	"inner"
//Undeylying ring (OUTER) (visual only)
        system.addRingBand(		star,			//ORBITAL ENTITY
                "misc",			//visual cat (settings.json)
                "rings_dust0",	//effect
                256f,			//width
                2,			//which one (see img)
                Color.gray,
                256f,			//width
                6150,			//distance
                110f);			//orbital period

        system.addRingBand(		star,			//ORBITAL ENTITY
                "misc",			//visual cat (settings.json)
                "rings_ice0",	//effect
                256f,			//width
                0,			//which one (see img)
                Color.gray,
                256f,			//width
                6250,			//distance
                104f);			//orbital period

//set nebula

        StarSystemGenerator.addSystemwideNebula(system, StarAge.OLD);

// Add a few random planets on the outskirts

        float radiusAfter = StarSystemGenerator.addOrbitingEntities(system, star, StarAge.YOUNG,
                2, 3, // min/max entities to add
                7500, // radius to start adding at
                2, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                true, // whether to use custom or system-name based names
                false); // whether to allow habitable worlds

//set hyperspace jump points

        system.autogenerateHyperspaceJumpPoints(true, true);

    }

}
