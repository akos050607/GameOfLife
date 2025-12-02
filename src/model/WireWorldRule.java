package model;

/**
 * A Wireworld sejtautomata szabályait megvalósító osztály
 */
public class WireWorldRule implements SimulationRule {

	/**
     * Kiszámolja a következő állapotot a 4 Wireworld szabály alapján.
     * 1. Üres -> Üres
     * 2. Fej -> Farok
     * 3. Farok -> Vezető
     * 4. Vezető -> Fej (ha 1 vagy 2 Fej szomszédja van)
     */
    @Override
    public CellState calculateNextState(Grid grid, int x, int y) {
        CellState currentState = grid.getCell(x, y);

        switch (currentState) {
            case EMPTY:
                // 1. szabály: Az Üres cella mindig Üres marad
                return CellState.EMPTY;

            case HEAD:
                // 2. szabály: Az Elektronfej a következő lépésben Elektronfarok lesz
                return CellState.TAIL;

            case TAIL:
                // 3. szabály: Az Elektronfarok a következő lépésben Vezető lesz
                return CellState.CONDUCTOR;

            case CONDUCTOR:
                // 4. szabály: A Vezető akkor lesz Fej, ha 1 vagy 2 Fej szomszédja van
                int headNeighbors = countHeadNeighbors(grid, x, y);
                if (headNeighbors == 1 || headNeighbors == 2) {
                    return CellState.HEAD;
                } else {
                    return CellState.CONDUCTOR;
                }

            default:
                return CellState.EMPTY;
        }
    }

    
    /*
     * A szomszédos fejeket megszámoljuk
     */
    private int countHeadNeighbors(Grid grid, int x, int y) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
            	//fura, de ezzel tudjuk azt ellenőrizni, hogy átlósan ne legyen számolva
                if ((i == 0 && j == 0) || !(i==0 || j==0)) continue;

                if (grid.getCell(x + j, y + i) == CellState.HEAD) {
                    count++;
                }
            }
        }
        return count;
    }
}
