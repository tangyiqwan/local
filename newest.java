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
