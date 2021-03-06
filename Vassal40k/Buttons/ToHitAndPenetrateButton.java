package Vassal40k.Buttons;

import Vassal40k.Utility.Chatbox;

public class ToHitAndPenetrateButton extends ToHitBaseButton
{
    private int strength = 4;
    private int armour = 10;
    private boolean armourbane = false;     //2D6 instead of 1D6
    private boolean gauss = false;          //Glancing on a 6, unless already a Pen
    private boolean lance = false;          //Vehicle armour is capped at 12
    private boolean melta = false;          //+1D6 (at half range)
    private boolean ordnance = false;       //Roll 2D6 and pick highest (how does this stack with armourbane?
    private boolean rending = false;        //Add a D3 on a 6
    private boolean tankHunters = false;    //Re-roll failed hits and may choose to re-roll glancing

    public ToHitAndPenetrateButton()
    {
        Initialize("To Hit/Penetrate", "To Hit/Penetrate", "To Hit/Penetrate", true);
        AddAttribute("strength", "Strength ", Integer.class, new BaseButton.ButtonAttributeMethods()
        {
            @Override public String getter() { return String.valueOf(strength); }
            @Override public void setter (Object o)
            {
                if ((o instanceof Integer))
                    strength = ((Integer)o).intValue();
                else if ((o instanceof String))
                    strength = Integer.parseInt((String)o);
            }
        });
        AddAttribute("armour", "Armour ", Integer.class, new BaseButton.ButtonAttributeMethods()
        {
            @Override public String getter() { return String.valueOf(armour); }
            @Override public void setter (Object o)
            {
                if ((o instanceof Integer))
                    armour = ((Integer)o).intValue();
                else if ((o instanceof String))
                    armour = Integer.parseInt((String)o);
            }
        });
        AddAttribute("armourbane", "Armourbane ", Boolean.class, new BaseButton.ButtonAttributeMethods()
        {
            @Override public String getter() { return String.valueOf(armourbane); }
            @Override public void setter (Object o)
            {
                if ((o instanceof Boolean))
                    armourbane = ((Boolean)o).booleanValue();
                else if ((o instanceof String))
                    armourbane = o.equals("true");
            }

        });
        AddAttribute("gauss", "Gauss ", Boolean.class, new BaseButton.ButtonAttributeMethods()
        {
            @Override public String getter() { return String.valueOf(gauss); }
            @Override public void setter (Object o)
            {
                if ((o instanceof Boolean))
                    gauss = ((Boolean)o).booleanValue();
                else if ((o instanceof String))
                    gauss = o.equals("true");
            }

        });
        AddAttribute("lance", "Lance ", Boolean.class, new BaseButton.ButtonAttributeMethods()
        {
            @Override public String getter() { return String.valueOf(lance); }
            @Override public void setter (Object o)
            {
                if ((o instanceof Boolean))
                    lance = ((Boolean)o).booleanValue();
                else if ((o instanceof String))
                    lance = o.equals("true");
            }

        });
        AddAttribute("melta", "Melta ", Boolean.class, new BaseButton.ButtonAttributeMethods()
        {
            @Override public String getter() { return String.valueOf(melta); }
            @Override public void setter (Object o)
            {
                if ((o instanceof Boolean))
                    melta = ((Boolean)o).booleanValue();
                else if ((o instanceof String))
                    melta = o.equals("true");
            }

        });
        AddAttribute("ordnance", "Ordnance ", Boolean.class, new BaseButton.ButtonAttributeMethods()
        {
            @Override public String getter() { return String.valueOf(ordnance); }
            @Override public void setter (Object o)
            {
                if ((o instanceof Boolean))
                    ordnance = ((Boolean)o).booleanValue();
                else if ((o instanceof String))
                    ordnance = o.equals("true");
            }

        });
        AddAttribute("rending", "Rending ", Boolean.class, new BaseButton.ButtonAttributeMethods()
        {
            @Override public String getter() { return String.valueOf(rending); }
            @Override public void setter (Object o)
            {
                if ((o instanceof Boolean))
                    rending = ((Boolean)o).booleanValue();
                else if ((o instanceof String))
                    rending = o.equals("true");
            }

        });
        AddAttribute("tankHunters", "Tank Hunters ", Boolean.class, new BaseButton.ButtonAttributeMethods()
        {
            @Override public String getter() { return String.valueOf(tankHunters); }
            @Override public void setter (Object o)
            {
                if ((o instanceof Boolean))
                    tankHunters = ((Boolean)o).booleanValue();
                else if ((o instanceof String))
                    tankHunters = o.equals("true");
            }

        });
    }

    @Override
    protected void OnClick ()
    {
        if (numberOfAttacks <= 0)
        {
            Chatbox.WriteLine("Number of Attacks must be 1 or more.");
            return;
        }

        Chatbox.WriteLine("<" + Chatbox.GetPlayerName() + "> rolls " + numberOfAttacks + " " + Pluralize("time", numberOfAttacks) + " for Vehicle Penetration:");

        int hits = RollToHit(numberOfAttacks);

        if (hits > 0)
        {
            Chatbox.WriteLine("<" + Chatbox.GetPlayerName() + "> rolls to Penetrate for " + hits + " " + Pluralize("hit", hits) + ":");
            RollToPenetrate(hits);
        }
    }

    protected void RollToPenetrate(int hits)
    {
        int penetrating = 0;
        int glancing = 0;

        for (int i = 0; i < hits; i++)
        {
            int roll = DiceRoll(6);
            String out = "* ";
            int tempArmour = armour;

            if (ordnance)
            {
                //How does this work with Armourbane/Melta?
                int secondRoll = DiceRoll(6);
                out += "Ordnance Weapon rolls (" + roll + ", " + secondRoll + "), taking highest (";
                if (secondRoll > roll)
                    roll = secondRoll;
                out += roll + ") + " + strength + " (Strength)";
            }
            else
            {
                out += "(" + roll + ") + " + strength + " (Strength)";
            }
            int originalRoll = roll;

            if (armourbane)
            {
                int armourbaneRoll = DiceRoll(6);
                out += " + " + armourbaneRoll + " (Armourbane)";
                roll += armourbaneRoll;
            }
            if (melta)
            {
                int meltaRoll = DiceRoll(6);
                out += " + " + meltaRoll + " (Melta)";
                roll += meltaRoll;
            }
            if (rending)
            {
                if (originalRoll == 6)
                {
                    int rendingRoll = DiceRoll(3);
                    out += " + " + rendingRoll + " (Rending)";
                    roll += rendingRoll;
                }
            }

            out += " = (" + (roll + strength) + ")";

            if (lance && tempArmour > 12)
            {
                tempArmour = 12;
                out += " vs. Armour " + tempArmour + " (reduced due to Lance)";
            }
            else
            {
                out += " vs. Armour " + tempArmour;
            }

            if (roll + strength > tempArmour)  //Penetrating
            {
                out += ": Penetrating Hit!";

                penetrating++;
            }
            else if ((roll + strength == tempArmour) || (gauss && originalRoll == 6))  //Glancing
            {
                out += ": Glancing Hit";
                if (gauss && roll + strength < tempArmour)  //Really a failure, but glances due to Gauss
                    out += " (due to Gauss)";
                out += ".";
                if (tankHunters)
                    out += " You may re-roll due to Tank Hunters (if this is not a re-roll) but must take the next result.";

                glancing++;
            }
            else if (roll + strength < tempArmour)  //Failed
            {
                out += ": Failed to penetrate";
                if (tankHunters)
                    out += ", but you may re-roll due to Tank Hunters (if this is not a re-roll).";
                else
                    out += ".";
            }

            Chatbox.WriteLine(out);
        }

        Chatbox.WriteLine("* Resulting in " +
                          penetrating + " Penetrating " + Pluralize("Hit", penetrating) + " and " +
                          glancing + " Glancing " + Pluralize("Hit", glancing));
    }
}