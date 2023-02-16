package pl.edu.wszib.mpudelko.library.book;

public class Book {
    private int id;
    private String author;
    private String title;
    private String isbn;
    private boolean isLoanable;

    public Book() {
    }

    public Book(String author, String title, String isbn) {
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.isLoanable = true;
    }

    public Book(String author, String title, String isbn, boolean isLoanable) {
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.isLoanable = isLoanable;
    }

    public Book(int id, String author, String title, String isbn, boolean isLoanable) {
        this.id = id;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.isLoanable = isLoanable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public boolean isLoanable() {
        return isLoanable;
    }

    public void setLoanable(boolean loanable) {
        isLoanable = loanable;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj == null)
            return false;

        if(this.getClass() != obj.getClass())
            return false;

        Book otherBook = (Book) obj;

        return this.id == otherBook.getId() && this.author.equals(otherBook.getAuthor())
                && this.title.equals(otherBook.getTitle()) && this.isbn.equals(otherBook.getIsbn())
                && this.isLoanable == otherBook.isLoanable();
    }

    @Override
    public String toString() {
        return String.format("%-50s %-40s %-13s %-10s", this.title, this.author, this.isbn, this.isLoanable);
    }
}
