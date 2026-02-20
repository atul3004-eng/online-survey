package dao;
import beans.Question;
import beans.Topic;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class TopicDAO {
    public static ArrayList<Topic> getTopics() {
        try {
            Connection con = Database.getConnection();
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from topics order by topictitle");
            ArrayList<Topic> al = new ArrayList<Topic>();
            while (rs.next()) {
                al.add( new Topic(rs.getString("topicid"), rs.getString("topictitle")));
            }
            rs.close();
            return al;
        } catch (Exception e) {
            System.out.println("Error In TopicDAO.getTopics() -->" + e.getMessage());
            return (null);
        }
    }

    public static ArrayList<Question> getQuestions(String topicid) {
        try {
            Connection con = Database.getConnection();
            PreparedStatement ps = con.prepareStatement("select * from questions where topicid = ? order by questionid");
            ps.setString(1, topicid);
            ResultSet rs = ps.executeQuery();

            ArrayList<Question> al = new ArrayList<Question>();
            while (rs.next()) {
                al.add(new Question(rs.getString("questionid"), rs.getString("questiontext"), rs.getString("opt1"), rs.getString("opt2"), rs.getString("opt3")));
            }
            rs.close();
            return al;
        } catch (Exception e) {
            System.out.println("Error In TopicDAO.getQuestions() -->" + e.getMessage());
            return (null);
        }
    }

     public static boolean storeSurveyResults(String topicid, ArrayList<Question> questions) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = Database.getConnection();
            con.setAutoCommit(false);
            ps = con.prepareStatement("insert into answers_master values (?, ?, now())", Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, null);
            ps.setString(2, topicid);
            ps.executeUpdate();

            ResultSet keyResultSet = ps.getGeneratedKeys();
            if (!keyResultSet.next()) {
                throw new IllegalStateException("Unable to read generated survey id");
            }
            long surveyId = keyResultSet.getLong(1);
            keyResultSet.close();

            ps = con.prepareStatement("insert into answers_details values( ?, ?, ?)");

            for( Question q : questions) {
                ps.setLong(1, surveyId);
                ps.setString(2, q.getId());
                ps.setString(3, q.getAnswer());
                ps.executeUpdate();
            }
            con.commit();
            return true;
        } catch (Exception ex) {
            System.out.println("Error in TopicDAO.storeSurveyResults() -->" + ex.getMessage());
            try {
                con.rollback();
            } catch (Exception nex) {
            }
            return false;
        } finally {
            Database.close(con);
        }
    }
}
