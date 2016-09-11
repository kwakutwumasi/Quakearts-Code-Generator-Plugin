package com.quakearts.webapp.beans;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;
import javax.faces.event.ActionEvent;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import com.quakearts.webapp.facelets.base.BaseBean;
import com.quakearts.webapp.hibernate.HibernateHelper;
import com.quakearts.test.hibernate.Guests;
import com.quakearts.test.hibernate.Menu;
import com.quakearts.test.hibernate.Party;
import com.quakearts.test.hibernate.Seating;
import com.quakearts.test.hibernate.SeatingType;

@ManagedBean(name="crudapp")
@ViewScoped
public class CRUDApplicationBean extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5797127755522296661L;
	private static Logger log = Logger.getLogger(CRUDApplicationBean.class.getName());
	private String mode;
	private Converter converter;
	
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public Converter getConverter() {
		if(converter==null){
			converter = new TimeStampConverter();
			((TimeStampConverter)converter).setDateStyle("dd/MM/yyyy");
		}
		return converter;
	}
	
	public static class TimeStampConverter extends DateTimeConverter {
		@Override
		public Object getAsObject(FacesContext context, UIComponent component, String dateString) {
			Object result;
			try {
				result = super.getAsObject(context, component, dateString);
				if (result instanceof Date) {
					//make it a Timestamp, because that is what jBPM will make of it anyway
					result = new java.sql.Timestamp(((Date) result).getTime());
				}
			} catch (ConverterException ex) {
				log.severe("Exception of type " + ex.getClass().getName()
						+ " was thrown. Message is " + ex.getMessage());
				return null;
			}
			return result;
		}

		@Override
		public String getAsString(FacesContext context, UIComponent component, Object dateObject) {
			String result = null;
			try {
				result = super.getAsString(context, component, dateObject);
			} catch (ConverterException ex) {
				return null;
			}
			return result;
		}
	}
	
	private Guests guests;	
	private List<Guests> guestsList;
	
	public Guests getGuests() {
		if(guests==null)
			guests = new Guests();
		
		return guests;
	}
	
	public void setGuests(Guests guests) {
		this.guests = guests;
	}
	
	public List<Guests> getGuestsList(){
		return guestsList;
	}
	
	@SuppressWarnings("unchecked")
	public void findGuests(ActionEvent event){
		Criteria query = HibernateHelper.getCurrentSession().createCriteria(Guests.class);
		if(guests.getId() != null && ! guests.getId().trim().isEmpty()){
			query.add(Restrictions.ilike("id", guests.getId()));
		}
		if(guests.getName() != null && ! guests.getName().trim().isEmpty()){
			query.add(Restrictions.ilike("name", guests.getName()));
		}
		if(guests.isValid()){
			query.add(Restrictions.eq("valid", guests.isValid()));
		}
		guestsList = query.list();
	}
	
	public void removeGuests(ActionEvent event){
		if(guests!=null && guestsList!=null){
			guestsList.remove(guests);
		}
	}
	
	private Menu menu;	
	private List<Menu> menuList;
	
	public Menu getMenu() {
		if(menu==null)
			menu = new Menu();
		
		return menu;
	}
	
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	public List<Menu> getMenuList(){
		return menuList;
	}
	
	@SuppressWarnings("unchecked")
	public void findMenu(ActionEvent event){
		Criteria query = HibernateHelper.getCurrentSession().createCriteria(Menu.class);
		if(menu.getItemName() != null && ! menu.getItemName().trim().isEmpty()){
			query.add(Restrictions.ilike("itemName", menu.getItemName()));
		}
		if(menu.isValid()){
			query.add(Restrictions.eq("valid", menu.isValid()));
		}
		menuList = query.list();
	}
	
	public void removeMenu(ActionEvent event){
		if(menu!=null && menuList!=null){
			menuList.remove(menu);
		}
	}
	
	private Party party;	
	private List<Party> partyList;
	
	public Party getParty() {
		if(party==null)
			party = new Party();
		
		return party;
	}
	
	public void setParty(Party party) {
		this.party = party;
	}
	
	public List<Party> getPartyList(){
		return partyList;
	}
	
	@SuppressWarnings("unchecked")
	public void findParty(ActionEvent event){
		Criteria query = HibernateHelper.getCurrentSession().createCriteria(Party.class);
		if(party.getGuests() != null){
			query.add(Restrictions.eq("guests", party.getGuests()));
		}
		if(party.getMenu() != null){
			query.add(Restrictions.eq("menu", party.getMenu()));
		}
		if(party.getSeating() != null){
			query.add(Restrictions.eq("seating", party.getSeating()));
		}
		if(party.isValid()){
			query.add(Restrictions.eq("valid", party.isValid()));
		}
		partyList = query.list();
	}
	
	public void removeParty(ActionEvent event){
		if(party!=null && partyList!=null){
			partyList.remove(party);
		}
	}
	
	private Seating seating;	
	private List<Seating> seatingList;
	
	public Seating getSeating() {
		if(seating==null)
			seating = new Seating();
		
		return seating;
	}
	
	public void setSeating(Seating seating) {
		this.seating = seating;
	}
	
	public List<Seating> getSeatingList(){
		return seatingList;
	}
	
	@SuppressWarnings("unchecked")
	public void findSeating(ActionEvent event){
		Criteria query = HibernateHelper.getCurrentSession().createCriteria(Seating.class);
		if(seating.getGuests() != null){
			query.add(Restrictions.eq("guests", seating.getGuests()));
		}
		if(seating.getSeatingType() != null){
			query.add(Restrictions.eq("seatingType", seating.getSeatingType()));
		}
		if(seating.isValid()){
			query.add(Restrictions.eq("valid", seating.isValid()));
		}
		seatingList = query.list();
	}
	
	public void removeSeating(ActionEvent event){
		if(seating!=null && seatingList!=null){
			seatingList.remove(seating);
		}
	}
	
	private SeatingType seatingtype;	
	private List<SeatingType> seatingtypeList;
	
	public SeatingType getSeatingtype() {
		if(seatingtype==null)
			seatingtype = new SeatingType();
		
		return seatingtype;
	}
	
	public void setSeatingtype(SeatingType seatingtype) {
		this.seatingtype = seatingtype;
	}
	
	public List<SeatingType> getSeatingtypeList(){
		return seatingtypeList;
	}
	
	@SuppressWarnings("unchecked")
	public void findSeatingtype(ActionEvent event){
		Criteria query = HibernateHelper.getCurrentSession().createCriteria(SeatingType.class);
		if(seatingtype.getDescription() != null && ! seatingtype.getDescription().trim().isEmpty()){
			query.add(Restrictions.ilike("description", seatingtype.getDescription()));
		}
		if(seatingtype.isValid()){
			query.add(Restrictions.eq("valid", seatingtype.isValid()));
		}
		seatingtypeList = query.list();
	}
	
	public void removeSeatingtype(ActionEvent event){
		if(seatingtype!=null && seatingtypeList!=null){
			seatingtypeList.remove(seatingtype);
		}
	}
	
}
