package reservation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;

public class ReservationManager {

	private static ArrayList<Reservation> reservations;
	private static HashMap<Integer, ArrayList<Reservation>> getResvByTableID;
	private static final int ADVANCERESERVATION = 3;// HOURS
	private static final int OPERATIONSTART = 11;// 11AM
	private static final int OPERATIONEND = 21;// 9PM, latest reservation can only be accepted until 8pm
	private static int idAssigner = 1;

	// Constructor
	static {
		reservations = new ArrayList<Reservation>();
		for (int i = 0; i < reservations.size(); i++) {
			reservations.get(i).setReservationID(idAssigner++);
		}
		getResvByTableID = new HashMap<Integer, ArrayList<Reservation>>();
		for (int i = 1; i <= TableManager.getNumberOfTables(); i++) {
			getResvByTableID.put(i, new ArrayList<Reservation>());
		}
		int tableID;
		for (int i = 0; i < reservations.size(); i++) {
			tableID = reservations.get(i).getReservationTable();
			getResvByTableID.get(tableID).add(reservations.get(i));
		}
		automaticRemoveReservations();
	}

	public static void setReservationArray(ArrayList<Reservation> newReservationArray) {
		reservations = newReservationArray;
	}

	public static ArrayList<Reservation> getReservationArray() {
		return reservations;
	}

	public static int findAvailableTable(int pax, LocalDateTime bookingTime, int minutesDifference) {
		/*
		 * Check if the reservation is available & assign it the best fit Table
		 * Arguments: pax (int): number of seats in reservation bookingTime
		 * (LocalDateTime) minutesDifference (float): 59 (maximum eating time = 1hr)
		 * Returns: bestTableID (int): best fit Table, if not available return 1
		 */
		long minuteDifference = (long) minutesDifference;
		int size, i, j;
		int bestTableID = -1;
		int bestTableSize = 99999;
		boolean thisTable;
		LocalDateTime t;
		// Find the best fit table
		for (i = 0; i < TableManager.getNumberOfTables(); i++) {
			if (TableManager.getCapacityByID(i) >= pax && TableManager.getCapacityByID(i) < bestTableSize) {
				// If tableSize is < current table, check if the reservation is available
				int tableID = i + 1;
				size = getResvByTableID.get(tableID).size();
				j = 0;
				thisTable = true;
				while (j < size) {
					// If the booking time clash with existed reservation -> not choose this table
					t = getResvByTableID.get(tableID).get(j).getBookingTime();
					if (bookingTime.isAfter(t.minusMinutes(minuteDifference))
							&& bookingTime.isBefore(t.plusMinutes(minuteDifference))) {
						thisTable = false;
						break;
					}
					j++;
				}
				// Change the bestTableID & bestCapSize
				if (thisTable) {
					bestTableID = i + 1;
					bestTableSize = TableManager.getCapacityByID(i);
				}
			}
		}
		return bestTableID;
	}

	public static void checkTableAvailability() {
		int pax = ReservationBoundary.askPax();
		LocalDateTime bookingTime = ReservationBoundary.askBookingTime();
		if (bookingTime.getHour() < OPERATIONSTART || bookingTime.getHour() > OPERATIONEND - 1) {
			ReservationBoundary.printBookingErrorTwo();
			return;
		}

		int tableID = findAvailableTable(pax, bookingTime, 59);
		if (tableID != -1) {
			ReservationBoundary.foundAvailableTable(tableID);
		} else {
			ReservationBoundary.noAvailableTable();
		}
	}

	public static void createReservation() {
		int pax = ReservationBoundary.askPax();
		LocalDateTime bookingTime;

		while (true) {
			// User input
			bookingTime = ReservationBoundary.askBookingTime();

			//// change the if to while?so that no need to ask for input again
			if (bookingTime.isBefore(LocalDateTime.now().plusHours(ADVANCERESERVATION))) {
				ReservationBoundary.printBookingErrorOne(ADVANCERESERVATION);
			} else if (bookingTime.getHour() < 11 || bookingTime.getHour() > 20) {
				ReservationBoundary.printBookingErrorTwo();
			} else
				break;
		}

		int tableID = findAvailableTable(pax, bookingTime, 59);
		if (tableID != -1) {
			String CustomerName = ReservationBoundary.askCustomerName();
			String contact = ReservationBoundary.askCustomerContact();
			Reservation reservation = new Reservation(CustomerName, contact, pax, tableID, bookingTime, idAssigner++);
			getResvByTableID.get(tableID).add(reservation);
			reservations.add(reservation);
			ReservationBoundary.successfulBooking();
			ReservationBoundary.printHeader();
			ReservationBoundary.printReservationDetails(reservation, 1);
		} else {
			ReservationBoundary.failedBooking();
		}
	}

	////////////////////////////////

	// should i add a function to remove reservation if the customer wish to cancel
	private static void automaticRemoveReservationsHelper() {
		// Remove all expired reservations
		for (int i = 0; i < reservations.size(); i++) {
			boolean expired = true; // exceeding 30 min will be removed
			if (expired == (LocalDateTime.now()).isAfter(reservations.get(i).getExpiryTime())) {
				int tableID = reservations.get(i).getReservationTable();
				// check which one in the arraylist should be removed, need to find the index
				int size = getResvByTableID.get(tableID).size();
				int index = -1;
				for (int j = 0; j < size; j++) {
					if (getResvByTableID.get(tableID).get(j) == reservations.get(i))
						index = j;
				}
				getResvByTableID.get(tableID).remove(index);
				reservations.remove(i);
				i--;
			}
		}
	}

	public static void automaticRemoveReservations() {
		Runnable automaticRemoveReservationsHelper = () -> automaticRemoveReservationsHelper();
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(automaticRemoveReservationsHelper, 0, 60, TimeUnit.SECONDS);
	}

	/////////////////////////////////
	public static ArrayList<Reservation> getReservationByContact(String contact) {
		ArrayList<Reservation> getReservationByContact = new ArrayList<Reservation>();
		for (int i = 0; i < reservations.size(); i++) {
			if (contact.equals(reservations.get(i).getContact())) {
				getReservationByContact.add(reservations.get(i));
			}
		}
		return getReservationByContact;
	}

	/* print reservation by contact or by name */
	public static void printReservationByContact() {
		String contact = ReservationBoundary.askCustomerContact();
		ArrayList<Reservation> getReservationByContactArrayList = getReservationByContact(contact);
		if (getReservationByContactArrayList.size() == 0)
			ReservationBoundary.bookingNotFound();
		/**
		 * else if (getReservationByContactArrayList.size()==1) {
		 * ReservationBoundary.bookingFound();
		 * ReservationBoundary.printSingleReservation(getReservationByContactArrayList.get(0));
		 * }
		 */
		else {
			ReservationBoundary.bookingFound();
			ReservationBoundary.printHeader();
			for (int i = 0; i < getReservationByContactArrayList.size(); i++) {
				// ReservationBoundary.printMultipleReservations(getReservationByContactArrayList.get(i),
				// i+1);

				ReservationBoundary.printReservationDetails(getReservationByContactArrayList.get(i), i + 1);
			}
		}
	}

	public static void printAllReservation() {
		if (reservations.size() == 0)
			ReservationBoundary.emptyReservation();
		else {
			ReservationBoundary.printHeader();
			for (int i = 0; i < reservations.size(); i++) {
				// printMultipleReservations(reservations.get(i), i+1);
				ReservationBoundary.printReservationDetails(reservations.get(i), i + 1);
			}
		}
	}

	public static void removeReservationByContact() {
		String contact = ReservationBoundary.askCustomerContact();
		ArrayList<Reservation> getReservationByContactArrayList = getReservationByContact(contact);
		int choice = 1;
		if (getReservationByContactArrayList.size() == 0) {
			ReservationBoundary.bookingNotFound();
			return;
		}

		else {
			ReservationBoundary.bookingFound();
			ReservationBoundary.printHeader();

			for (int i = 0; i < getReservationByContactArrayList.size(); i++) {
				ReservationBoundary.printReservationDetails(getReservationByContactArrayList.get(i), i + 1);
			}
			if (getReservationByContactArrayList.size() > 1)
				choice = ReservationBoundary.removeChoice(getReservationByContactArrayList.size()); // Interface
			// trigger remove
			int i, j;
			for (i = 0; i < reservations.size(); i++) {
				if (getReservationByContactArrayList.get(choice - 1).getReservationID() == reservations.get(i)
						.getReservationID())
					break;
			}
			int tableID = reservations.get(i).getReservationTable();
			int size = getResvByTableID.get(tableID).size();
			for (j = 0; j < size; j++) {
				if (getResvByTableID.get(tableID).get(j).getReservationID() == reservations.get(i).getReservationID()) {
					getResvByTableID.get(tableID).remove(j);
					reservations.remove(i);
					ReservationBoundary.removeTriggered(); // Interface
					return;
				}
			}
		}
	}
}

//////////////////////////
