package mz.maleyanga

class GenerateRandomNumber {
    public static String getRandomNumberString() {
        // It will generate 4 digit random Number.
        // from 0 to 999999
        Random rnd = new Random()
        int number1 = rnd.nextInt(99)
        int number2 = rnd.nextInt(99)

        // this will convert any number sequence into 6 character.
        String codigo = String.format("%04d", number2) + "-" + String.format("%04d", number1)

        return codigo
    }

    public static String getRandomBigNumberString() {
        // It will generate 4 digit random Number.
        // from 0 to 999999
        Random rnd = new Random()
        int number1 = rnd.nextInt(9999)
        //  int number2= rnd.nextInt(9999)
        // int number3= rnd.nextInt(9999)

        // this will convert any number sequence into 6 character.
        // String codigo = String.format("%04d", number2)+String.format("%04d", number1)
        String codigo = String.format("%04d", number1)

        return codigo
    }

    public static String getRandomHexString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random()
        int number = rnd.nextInt(999999)
        def result = Integer.toHexString(number)
        // this will convert any number sequence into 6 character.
        String codigo = String.format("%06d", result)

        return codigo
    }
}
