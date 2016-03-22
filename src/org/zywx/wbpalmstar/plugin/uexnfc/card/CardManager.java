/* NFCard is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NFCard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package org.zywx.wbpalmstar.plugin.uexnfc.card;

import org.zywx.wbpalmstar.plugin.uexnfc.card.pboc.PbocCard;

import android.content.res.Resources;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Parcelable;

public final class CardManager {

	private static final String SP = "<br />------------------------------</b><br />";

	public static String buildResult(String n, String i, String d, String x) {
		if (n == null)
			return null;

		final StringBuilder s = new StringBuilder();

		s.append(n);

		if (d != null)
			s.append(SP).append(d);

		if (x != null)
			s.append(SP).append(x);

		if (i != null)
			s.append(SP).append(i);

		return s.toString();
	}

	public static String load(Parcelable parcelable, Resources res) {
		final Tag tag = (Tag) parcelable;

		final IsoDep isodep = IsoDep.get(tag);
		if (isodep != null) {
			return PbocCard.load(isodep, res);
		}

		final NfcV nfcv = NfcV.get(tag);
		if (nfcv != null) {
			return VicinityCard.load(nfcv, res);
		}

		final NfcF nfcf = NfcF.get(tag);
		if (nfcf != null) {
			return OctopusCard.load(nfcf, res);
		}

		return null;
	}
}
