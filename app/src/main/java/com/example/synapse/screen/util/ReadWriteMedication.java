package com.example.synapse.screen.util;

public class ReadWriteMedication {
    private String Name;
    private String Dose;
    private String Shape;
    private String PillColor;
    private String Time;

    public ReadWriteMedication() { }

    public ReadWriteMedication(String textName, String textDose, String textShape, String textPillColor, String textTime) {
        this.Name = textName;
        this.Dose = textDose;
        this.Shape = textShape;
        this.PillColor = textPillColor;
        this.Time = textTime;
    }

    public String getName(){ return Name; }
    public void setName(String Name){ this.Name = Name; }

    public String getDose(){ return Dose; }
    public void setDose(String Dose){ this.Dose = Dose; }

    public String getShape(){ return Shape; }
    public void setShape(String Shape){ this.Shape = Shape; }

    public String getColor(){ return PillColor; }
    public void setColor(String PillColor){ this.PillColor = PillColor; }

    public String getTime(){ return Time; }
    public void setTime(String Time){ this.Time = Time; }

}
