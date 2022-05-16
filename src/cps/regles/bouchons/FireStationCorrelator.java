package cps.regles.bouchons;

import java.util.ArrayList;

import cps.cep.fireStation.FireStationCorrelatorI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public class FireStationCorrelator 
implements FireStationCorrelatorI {

	public static final double N = 100.0;
	/**disponibilite de grande echelle 				*/
	private boolean echelleDispo;
	/**disponibilite de camion stantard 			*/
	private boolean camionDispo;
	/**Une liste de FireStation				 			*/
	private ArrayList<AbsolutePosition> listeSation = new ArrayList<AbsolutePosition>();
	
	public FireStationCorrelator(EventBaseI eb) {
		this.echelleDispo = true;
		this.camionDispo = true;
		this.listeSation.add(new AbsolutePosition(0.5, 1.0));
		this.listeSation.add(new AbsolutePosition(2.0, 3.5));
	}
	
	@Override
	public boolean estDansZone(AbsolutePosition p) {
		return inGrid(p.getX()) && inGrid(p.getY());
	}
	
	/**
	 * return true if {@code v} in in the smart city grid, false otherwise.
	 * @param v	coordinate to be tested.
	 * @return	true if {@code v} is in the smart city grid, false otherwise.
	 */
	public static boolean inGrid(double v)
	{
		return v >= 0.0 && v <= N;
	}
	
	
	/**
	 * return les coordonnees de la caserne la plus proche de la position donnee
	 * @param p: la position de la caserne donnee
	 * @return retourne la position trouvee si on en trouver un, sinon on retourne null
	 * */
	public AbsolutePosition plusProcheStation(AbsolutePosition p) throws Exception{
		//initialiser la distance minimum
		double minDist = 10000.0;
		AbsolutePosition tmp = new AbsolutePosition(0, 0);
		for(AbsolutePosition station: this.listeSation) {
			if(station.distance(p) < minDist) {
				tmp = station;
			}
		}
		if(!tmp.equals(p)) {
			return tmp;
		}else {
			System.out.println("Pas de caserne le plus proche");
			return null;
		}
	}

	/**
	 * Retourne la disponibilite de la grande echelle
	 * @return "true" si la grande echelle est disponible sinon "false"
	 * */
	@Override
	public boolean estEchelleDispo() {
		return this.echelleDispo;
	}
	
	
	/**
	 * Retourne la disponibilite du camion stantard
	 * @return "true" si le camion stantard est disponible sinon "false"
	 * */
	@Override
	public boolean estCamionDispo() {
		return this.camionDispo;
	}

	@Override
	public void triggerPremiereAlarmeImmeuble(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("First alarm triggered at " + pos +
				   " using resource grande echelle\n");
	}

	@Override
	public void triggerPremiereAlarmeMaison(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("First alarm triggered at " + pos +
				   " using resource stantard camion\n");
	}

	@Override
	public void triggerGeneraleAlarme(ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("General alarm triggered");
	}

	@Override
	public void triggerGeneraleAlarmePosition(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("General alarm triggered at " + pos + "\n");
	}

	@Override
	public void triggerSecondeAlarme(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("Second alarm triggered at " + pos + "\n");
	}

	@Override
	public void setEchelleDispo(boolean ed) throws Exception {
		this.echelleDispo = ed;
	}

	@Override
	public void setCamionDispo(boolean ecd) throws Exception {
		this.camionDispo = ecd;
	}

	@Override
	public EventBaseI getEb() throws Exception {
		return null;
	}
	
}
