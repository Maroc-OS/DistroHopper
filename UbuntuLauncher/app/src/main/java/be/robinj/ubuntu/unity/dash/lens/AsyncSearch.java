package be.robinj.ubuntu.unity.dash.lens;

import android.os.AsyncTask;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import be.robinj.ubuntu.thirdparty.ProgressWheel;

/**
 * Created by robin on 25/11/14.
 */
public class AsyncSearch extends AsyncTask<String, Integer, Object[]>
{
	private LensManager lensManager;

	private ProgressWheel progressWheel;
	private ListView lvDashHomeLensResults;

	public AsyncSearch (LensManager lensManager, ProgressWheel progressWheel, ListView lvDashHomeLensResults)
	{
		this.lensManager = lensManager;

		this.progressWheel = progressWheel;
		this.lvDashHomeLensResults = lvDashHomeLensResults;
	}

	@Override
	protected Object[] doInBackground (String... params)
	{
		String pattern = params[0];

		List<LensSearchResultCollection> results = new ArrayList<LensSearchResultCollection> ();
		if (pattern.length () > 0)
		{
			List<Lens> lenses = this.lensManager.getEnabledLenses ();
			int nLenses = lenses.size ();
			int maxResultsPerLens = lensManager.getMaxResultsPerLens ();

			for (int i = 0; i < nLenses; i++)
			{
				Lens lens = lenses.get (i);
				List<LensSearchResult> lensResults = null;

				try
				{
					lensResults = lens.search (pattern);
				}
				catch (Exception ex)
				{
					ex.printStackTrace ();
				}

				if (lensResults != null && lensResults.size () > 0)
				{
					lensResults = lensResults.subList (0, lensResults.size () > maxResultsPerLens ? maxResultsPerLens : lensResults.size ());

					LensSearchResultCollection collection = new LensSearchResultCollection (lens, lensResults);
					results.add (collection);
				}

				this.publishProgress (i, nLenses);
			}
		}

		return new Object[] { results };
	}

	@Override
	protected void onProgressUpdate (Integer... progress)
	{
		super.onProgressUpdate (progress[0]);

		this.progressWheel.setProgress ((int) ((float) progress[0] / (float) progress[1] * 360));
	}

	@Override
	protected void onPostExecute (Object[] result)
	{
		List<LensSearchResultCollection> results = (List<LensSearchResultCollection>) result[0];

		this.lvDashHomeLensResults.setAdapter (new be.robinj.ubuntu.unity.dash.lens.CollectionGridAdapter (this.lensManager.getContext (), results));
	}
}