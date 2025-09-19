package org.example;

public class Evaluacion {
    private int id;
    private String criterio;
    private int puntuacion;
    private String comentario;

    public Evaluacion(int id, String criterio, int puntuacion, String comentario) {
        this.id = id;
        this.criterio = criterio;
        setPuntuacion(puntuacion);
        setComentario(comentario);
    }

    public Evaluacion(String criterio) {
        this.criterio = criterio;
        this.puntuacion = 0;
        this.comentario = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        if (puntuacion >= 0 && puntuacion <= 10) {
            this.puntuacion = puntuacion;
        } else {
            throw new IllegalArgumentException("La puntuación debe estar entre 0 y 10");
        }
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario != null ? comentario : "";
    }

    @Override
    public String toString() {
        return "Evaluación de " + criterio + ": " + puntuacion + "/10" +
                (comentario.isEmpty() ? "" : " - " + comentario);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evaluacion)) return false;
        Evaluacion that = (Evaluacion) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}