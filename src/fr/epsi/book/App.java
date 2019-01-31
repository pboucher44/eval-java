package fr.epsi.book;

import fr.epsi.book.dal.BookDAO;
import fr.epsi.book.dal.ContactDAO;
import fr.epsi.book.domain.Book;
import fr.epsi.book.domain.Contact;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;



public class App {

	private static final String EXPORT_CSV_DIR = "./resources/CSV/";

	private static final Scanner sc = new Scanner(System.in);
	private static Book book = new Book();
	
	private static ContactDAO monContactDao = new ContactDAO();
	private static BookDAO monBookDao = new BookDAO();

	public static void main(String... args) throws SQLException {
		dspBooknMenu();
	}
	
	public static void dspBooknMenu() throws SQLException {
		int response;
		boolean first = true;
		do {
			if (!first) {
				System.out.println("***********************************************");
				System.out.println("* Mauvais choix, merci de recommencer !       *");
				System.out.println("***********************************************");
			}
			System.out.println("**************************************");
			System.out.println("*****************Menu*****************");
			System.out.println("*** 1 - Ajouter un book             **");
			System.out.println("*** 2 - Modifier un book            **");
			System.out.println("*** 3 - Supprimer un book           **");
			System.out.println("*** 4 - Quitter                     **");
			System.out.println("**************************************");
			System.out.print("*Votre choix : ");
			try {
				response = sc.nextInt();
			} catch (InputMismatchException e) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while (1 > response || 4 < response);
		switch (response) {
		case 1:
			monBookDao.create(book);
			dspMainMenu();
			break;
		case 2:
			restoreBooks();
			dspMainMenu();
			break;
		case 3:
			deleteBooks();
			dspBooknMenu();
			break;
		case 4:
			break;
		}
	}
	
	private static void deleteBooks() throws SQLException {
		List<Book> lesId = null;
		boolean first = true;
		int compteur = 1;
		int response = -1;
		lesId = monBookDao.findAll();
		
		if (lesId.size() != 0) {
			do {
				if (!first) {
					System.out.println("***********************************************");
					System.out.println("* Mauvais choix, merci de recommencer !       *");
					System.out.println("***********************************************");
				}
				System.out.println("**************************************");
				System.out.println("******* Quel book supprimer ? ********");

				for (Book id : lesId) {
					System.out.println(compteur + " - " + id.getId());
					compteur++;
				}
				System.out.println("**************************************");

				System.out.print("*Votre choix : ");

				try {
					response = sc.nextInt();
				} catch (InputMismatchException e) {
					response = -1;
				} finally {
					sc.nextLine();
				}
				first = false;

			} while (1 > response || lesId.size() < response);
		}
		if (lesId.size() != 0) {
			System.out.println("Restauration du book : " + lesId.get(response - 1));
			try {
				monBookDao.remove(lesId.get(response - 1).getId());
				System.out.println("suppression termin�e : book " + lesId.get(response - 1).getId());
			} catch (Exception e) {
				// TODO: handle exception
				System.err.println(e);
			}
		}else {
			System.out.println("Aucun book en base");
		}
}
	
	private static void restoreBooks() throws SQLException {
		List<Book> lesId = null;
		boolean first = true;
		int compteur = 1;
		int response = -1;
		lesId = monBookDao.findAll();
		
		if (lesId.size() != 0) {
			do {
				if (!first) {
					System.out.println("***********************************************");
					System.out.println("* Mauvais choix, merci de recommencer !       *");
					System.out.println("***********************************************");
				}
				System.out.println("**************************************");
				System.out.println("******* Quel book restaurer ? ********");

				for (Book id : lesId) {
					System.out.println(compteur + " - " + id.getId());
					compteur++;
				}
				System.out.println("**************************************");

				System.out.print("*Votre choix : ");

				try {
					response = sc.nextInt();
				} catch (InputMismatchException e) {
					response = -1;
				} finally {
					sc.nextLine();
				}
				first = false;

			} while (1 > response || lesId.size() < response);
		}
		if (lesId.size() != 0) {
			System.out.println("Restauration du book : " + lesId.get(response - 1));
			try {
				book = monBookDao.findById(lesId.get(response - 1).getId());
				System.out.println("Restauration termin�e : book " + lesId.get(response - 1).getId());
			} catch (Exception e) {
				// TODO: handle exception
				System.err.println(e);
			}
		}else {
			monBookDao.create(book);
			System.out.println("Aucun book en base nous vous en avons cr�� un nouveau");
		}
}

	public static Contact.Type getTypeFromKeyboard() {
		int response;
		boolean first = true;
		do {
			if (!first) {
				System.out.println("***********************************************");
				System.out.println("* Mauvais choix, merci de recommencer !       *");
				System.out.println("***********************************************");
			}
			System.out.println("*******Choix type de contact *******");
			System.out.println("* 1 - Perso                         *");
			System.out.println("* 2 - Pro                          *");
			System.out.println("************************************");
			System.out.print("*Votre choix : ");
			try {
				response = sc.nextInt() - 1;
			} catch (InputMismatchException e) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while (0 != response && 1 != response);
		return Contact.Type.values()[response];
	}

	public static void addContact() {
		System.out.println("**************************************");
		System.out.println("**********Ajout d'un contact**********");
		Contact contact = new Contact();
		System.out.print("Entrer le nom :");
		contact.setName(sc.nextLine());
		System.out.print("Entrer l'email :");
		contact.setEmail(sc.nextLine());
		System.out.print("Entrer le téléphone :");
		contact.setPhone(sc.nextLine());
		while(!Pattern.matches("^(0)[1-9](\\d{2}){4}$",contact.getPhone())) {
			System.out.print("Entrez un numero valide :");
			contact.setPhone(sc.nextLine());
		}
		contact.setType(getTypeFromKeyboard());
		book.addContact(contact);
		try {
			monContactDao.create(contact, book.getId());
		} catch (SQLException e) {
			System.err.println("erreur lors de l'insertion du contact dans la BDD");
			e.printStackTrace();
		}
		System.out.println("Nouveau contact ajouté ...");
	}

	public static void editContact() throws SQLException {
		System.out.println("*********************************************");
		System.out.println("**********Modification d'un contact**********");
		dspContacts(false);
		System.out.print("Entrer l'identifiant du contact : ");
		String id = sc.nextLine();
		Contact contact = book.getContacts().get(id);
		if (null == contact) {
			System.out.println("Aucun contact trouvé avec cet identifiant ...");
		} else {
			System.out.print("Entrer le nom ('" + contact.getName() + "'; laisser vide pour ne pas mettre à jour) : ");
			String name = sc.nextLine();
			if (!name.isEmpty()) {
				contact.setName(name);
			}
			System.out
					.print("Entrer l'email ('" + contact.getEmail() + "'; laisser vide pour ne pas mettre à jour) : ");
			String email = sc.nextLine();
			if (!email.isEmpty()) {
				contact.setEmail(email);
			}
			System.out.print(
					"Entrer le téléphone ('" + contact.getPhone() + "'; laisser vide pour ne pas mettre à jour) : ");
			String phone = sc.nextLine();
			if (!phone.isEmpty()) {
				contact.setPhone(phone);
			}
			monContactDao.update(contact);
			System.out.println("Le contact a bien été modifié ...");
		}
	}

	public static void deleteContact() throws SQLException {
		System.out.println("*********************************************");
		System.out.println("***********Suppression d'un contact**********");
		dspContacts(false);
		System.out.print("Entrer l'identifiant du contact : ");
		String id = sc.nextLine();
		Contact contact = book.getContacts().remove(id);
		if (null == contact) {
			System.out.println("Aucun contact trouvé avec cet identifiant ...");
		} else {
			monContactDao.remove(contact);
			System.out.println("Le contact a bien été supprimé ...");
		}
	}

	public static void sort() {
		int response;
		boolean first = true;
		do {
			if (!first) {
				System.out.println("***********************************************");
				System.out.println("* Mauvais choix, merci de recommencer !       *");
				System.out.println("***********************************************");
			}
			System.out.println("*******Choix du critère*******");
			System.out.println("* 1 - Nom     **              *");
			System.out.println("* 2 - Email **                *");
			System.out.println("*******************************");
			System.out.print("*Votre choix : ");
			try {
				response = sc.nextInt();
			} catch (InputMismatchException e) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while (0 >= response || response > 2);
		Map<String, Contact> contacts = book.getContacts();
		switch (response) {
		case 1:
			contacts.entrySet().stream()
					.sorted((e1, e2) -> e1.getValue().getName().compareToIgnoreCase(e2.getValue().getName()))
					.forEach(e -> dspContact(e.getValue()));
			break;
		case 2:

			contacts.entrySet().stream()
					.sorted((e1, e2) -> e1.getValue().getEmail().compareToIgnoreCase(e2.getValue().getEmail()))
					.forEach(e -> dspContact(e.getValue()));
			break;
		}
	}

	public static void searchContactsByName() {

		System.out.println("*******************************************************************");
		System.out.println("************Recherche de contacts sur le nom ou l'email************");
		System.out.println("*******************************************************************");
		System.out.print("*Mot clé (1 seul) : ");
		String word = sc.nextLine();
		Map<String, Contact> subSet = book.getContacts().entrySet().stream().filter(
				entry -> entry.getValue().getName().contains(word) || entry.getValue().getEmail().contains(word))
				.collect(HashMap::new, (newMap, entry) -> newMap.put(entry.getKey(), entry.getValue()), Map::putAll);

		if (subSet.size() > 0) {
			System.out.println(subSet.size() + " contact(s) trouvé(s) : ");
			subSet.entrySet().forEach(entry -> dspContact(entry.getValue()));
		} else {
			System.out.println("Aucun contact trouvé avec cet identifiant ...");
		}
	}

	public static void dspContact(Contact contact) {
		System.out.println(contact.getId() + "\t\t\t\t" + contact.getName() + "\t\t\t\t" + contact.getEmail()
				+ "\t\t\t\t" + contact.getPhone() + "\t\t\t\t" + contact.getType());
	}

	public static void dspContacts(boolean dspHeader) throws NumberFormatException, SQLException {
		if (dspHeader) {
			System.out.println("**************************************");
			System.out.println("********Liste de vos contacts*********");
		}
		for (Map.Entry<String, Contact> entry : monBookDao.findById(book.getId()).getContacts().entrySet()) {
			dspContact(entry.getValue());
		}
		System.out.println("**************************************");
	}

	public static void dspMainMenu() throws SQLException {
		int response;
		boolean first = true;
		do {
			if (!first) {
				System.out.println("***********************************************");
				System.out.println("* Mauvais choix, merci de recommencer !       *");
				System.out.println("***********************************************");
			}
			System.out.println("**************************************");
			System.out.println("*****************Menu*****************");
			System.out.println("* 1 - Ajouter un contact             *");
			System.out.println("* 2 - Modifier un contact            *");
			System.out.println("* 3 - Supprimer un contact           *");
			System.out.println("* 4 - Lister les contacts            *");
			System.out.println("* 5 - Rechercher un contact          *");
			System.out.println("* 6 - Trier les contacts             *");
			System.out.println("* 7 - Export des contacts            *");
			System.out.println("* 8 - Quitter                       *");
			System.out.println("**************************************");
			System.out.print("*Votre choix : ");
			try {
				response = sc.nextInt();
			} catch (InputMismatchException e) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while (1 > response || 10 < response);
		switch (response) {
		case 1:
			addContact();
			dspMainMenu();
			break;
		case 2:
			editContact();
			dspMainMenu();
			break;
		case 3:
			deleteContact();
			dspMainMenu();
			break;
		case 4:
			dspContacts(true);
			dspMainMenu();
			break;
		case 5:
			searchContactsByName();
			dspMainMenu();
			break;
		case 6:
			sort();
			dspMainMenu();
			break;
		case 7:
			exportContacts();
			dspMainMenu();
			break;
		case 8:
			break;
		}
	}

	private static void exportContacts() throws SQLException {
		boolean first = true;
		int response;
		do {
			if (!first) {
				System.out.println("***********************************************");
				System.out.println("* Mauvais choix, merci de recommencer !       *");
				System.out.println("***********************************************");
			}
			System.out.println("**************************************");
			System.out.println("*****************Menu*****************");
			System.out.println("* 1 - exporter en CSV                *");
			System.out.println("* 2 - exporter en XML                *");
			System.out.println("* 3 - retour                         *");
			System.out.println("**************************************");

			System.out.print("*Votre choix : ");

			try {
				response = sc.nextInt();
			} catch (InputMismatchException e) {
				response = -1;
			} finally {
				sc.nextLine();
			}
			first = false;
		} while (1 > response || 3 < response);
		switch (response) {
		case 1:
			exportCSV();
			dspMainMenu();
			break;
		case 2:
			exportXML();
			dspMainMenu();
			break;
		case 3:
			dspMainMenu();
			break;
		}
	}

	private static void exportCSV() {
		StringBuilder sb = new StringBuilder();
		String sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());

		try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(EXPORT_CSV_DIR + sdf + ".csv"))) {
			String lineTmp = null;

			bw.append("Nom");
			bw.append(',');
			bw.append("Email");
			bw.append('\n');

			for (Map.Entry<String, Contact> entry : book.getContacts().entrySet()) {
				bw.append(entry.getValue().getName());
				bw.append(',');
				bw.append(entry.getValue().getEmail());
				bw.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void exportXML() {
		try {
			JAXBContext context = JAXBContext.newInstance(Book.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(book, System.out);
			marshaller.marshal(book, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}