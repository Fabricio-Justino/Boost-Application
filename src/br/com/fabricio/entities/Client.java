package br.com.fabricio.entities;

import br.com.fabricio.anottation.Entity;
import br.com.fabricio.anottation.Setter;
import br.com.fabricio.anottation.UsingSetters;
import br.com.fabricio.anottation.constraint.Id;
import br.com.fabricio.anottation.constraint.NotNull;

@Entity
@UsingSetters
public class Client {

    @NotNull
    @Id
    private String cpf;

    private String name;

    private String lastName;
    private double balance;

    public Client() {

    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Client client = (Client) o;

        return getCpf() != null ? getCpf().equals(client.getCpf()) : client.getCpf() == null;
    }

    @Override
    public int hashCode() {
        return getCpf() != null ? getCpf().hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
                nome: %s %s
                cpf: %s
                """.formatted(this.name, this.lastName, this.cpf);
    }
}
