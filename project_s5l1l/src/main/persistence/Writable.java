/*
 *
 * This code was copied from JsonSerializationDemo provided by the course instructors of CPSC 210 at UBC
 *
 */

package persistence;

import org.json.JSONObject;

public interface Writable {
    // EFFECTS: returns this as JSON object
    JSONObject toJson();
}
