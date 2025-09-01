package advogados_popular.api_advogados_popular.Utils.chat;

public class RoomNormalizer {
    // Canonical: pair-u{userId}-a{lawyerId}
    public static String normalize(String room) {
        if (room == null) return null;
        room = room.trim();
        if (!room.startsWith("pair-")) return null;

        // Accept pair-u123-a45 or pair-a45-u123 strictly with numeric ids
        String rest = room.substring(5);
        String[] parts = rest.split("-");
        Long uId = null; Long aId = null;
        for (String p : parts) {
            if (p.startsWith("u")) {
                try { uId = Long.parseLong(p.substring(1)); } catch (Exception ignored) {}
            } else if (p.startsWith("a")) {
                try { aId = Long.parseLong(p.substring(1)); } catch (Exception ignored) {}
            }
        }
        if (uId == null || aId == null) return null;
        return "pair-u" + uId + "-a" + aId;
    }
}
