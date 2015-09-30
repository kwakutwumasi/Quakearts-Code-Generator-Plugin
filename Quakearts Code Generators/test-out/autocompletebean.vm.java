package com.quakearts.webapp.beans;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.AjaxBehaviorEvent;
import com.quakearts.webapp.hibernate.HibernateHelper;
import com.quakearts.test.hibernate.Guests;
import com.quakearts.test.hibernate.Menu;
import com.quakearts.test.hibernate.Party;
import com.quakearts.test.hibernate.Seating;
import com.quakearts.test.hibernate.SeatingType;

@ManagedBean(name="autocomplete")
@ViewScoped
public class AutoCompleteBean {
	
	private String guestsSearchText;
	private List<Guests> foundGuestsItems;
	
	public String getguestsSearchText() {
		return guestsSearchText;
	}
	
	public void setGuestsSearchText(String guestsSearchText) {
		this.guestsSearchText = guestsSearchText;
	}

	public List<Guests> getFoundGuestsItems() {
		return foundGuestsItems;
	}
	
	@SuppressWarnings("unchecked")	public void filterGuestsItems(AjaxBehaviorEvent event){
		if(!isEmpty(guestsSearchText)){
			foundGuestsItems = HibernateHelper.getCurrentSession().createQuery("select a from com.quakearts.test.hibernate.Guests a where "
					+"a.id like :id  or a.name like :name ")
					.setString("id", "%" + guestsSearchText + "%").setString("name", "%" + guestsSearchText + "%").list();
		}
	}
	
	private String menuSearchText;
	private List<Menu> foundMenuItems;
	
	public String getmenuSearchText() {
		return menuSearchText;
	}
	
	public void setMenuSearchText(String menuSearchText) {
		this.menuSearchText = menuSearchText;
	}

	public List<Menu> getFoundMenuItems() {
		return foundMenuItems;
	}
	
	@SuppressWarnings("unchecked")	public void filterMenuItems(AjaxBehaviorEvent event){
		if(!isEmpty(menuSearchText)){
			foundMenuItems = HibernateHelper.getCurrentSession().createQuery("select a from com.quakearts.test.hibernate.Menu a where "
					+"a.itemName like :itemName ")
					.setString("itemName", "%" + menuSearchText + "%").list();
		}
	}
	
	private String partySearchText;
	private List<Party> foundPartyItems;
	
	public String getpartySearchText() {
		return partySearchText;
	}
	
	public void setPartySearchText(String partySearchText) {
		this.partySearchText = partySearchText;
	}

	@SuppressWarnings("unchecked")	public List<Party> getFoundPartyItems() {
		foundPartyItems = HibernateHelper.getCurrentSession().createCriteria(Party.class).list();
		return foundPartyItems;
	}
	
	public void filterPartyItems(AjaxBehaviorEvent event){
	}
	
	private String seatingSearchText;
	private List<Seating> foundSeatingItems;
	
	public String getseatingSearchText() {
		return seatingSearchText;
	}
	
	public void setSeatingSearchText(String seatingSearchText) {
		this.seatingSearchText = seatingSearchText;
	}

	@SuppressWarnings("unchecked")	public List<Seating> getFoundSeatingItems() {
		foundSeatingItems = HibernateHelper.getCurrentSession().createCriteria(Seating.class).list();
		return foundSeatingItems;
	}
	
	public void filterSeatingItems(AjaxBehaviorEvent event){
	}
	
	private String seatingtypeSearchText;
	private List<SeatingType> foundSeatingtypeItems;
	
	public String getseatingtypeSearchText() {
		return seatingtypeSearchText;
	}
	
	public void setSeatingtypeSearchText(String seatingtypeSearchText) {
		this.seatingtypeSearchText = seatingtypeSearchText;
	}

	public List<SeatingType> getFoundSeatingtypeItems() {
		return foundSeatingtypeItems;
	}
	
	@SuppressWarnings("unchecked")	public void filterSeatingtypeItems(AjaxBehaviorEvent event){
		if(!isEmpty(seatingtypeSearchText)){
			foundSeatingtypeItems = HibernateHelper.getCurrentSession().createQuery("select a from com.quakearts.test.hibernate.SeatingType a where "
					+"a.description like :description ")
					.setString("description", "%" + seatingtypeSearchText + "%").list();
		}
	}
	
	private boolean isEmpty(String text){
		return text ==null||text.trim().isEmpty();
	}
}
