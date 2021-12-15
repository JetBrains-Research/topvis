from argparse import ArgumentParser
from tempfile import TemporaryDirectory

from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import CountVectorizer
from collections import Counter
import collections
import datetime
import json
import os


def get_files_list(repo):
    all_files = []
    for root, dirs, files in os.walk(repo):
        path = root[len(repo):]
        if not (path.startswith("/.git/") or path == "/.git"):
            if os.path.isfile(path):
                all_files.append(root)
            for file in files:
                all_files.append(root + '/' + file)
    return all_files


def mkdir(path: str) -> None:
    if not os.path.exists(path):
        os.mkdir(path)

    if not os.path.isdir(path):
        raise ValueError(f'{path} is not a directory!')


def sort_coords(coords_matrix):
    tuples = zip(coords_matrix.col, coords_matrix.data)
    return sorted(tuples, key=lambda x: (x[1], x[0]), reverse=True)


def extract_top_n_from_vector(feature_names, sorted_items, top_n=5):
    sorted_items = sorted_items[:top_n]
    score_values = []
    feature_values = []
    for idx, score in sorted_items:
        score_values.append(round(score, 3))
        feature_values.append(feature_names[idx])
    results = {}
    for idx in range(len(feature_values)):
        results[feature_values[idx]] = score_values[idx]
    return results


def tfidf(files):
    print('Counting tf-idf vector')
    cv = CountVectorizer(input='filename', max_df=0.25, strip_accents='unicode', decode_error='ignore')
    tf = TfidfTransformer(smooth_idf=True, use_idf=True)
    wc = cv.fit_transform(files)
    tf_idf_vector = tf.fit_transform(wc)
    feature_names = cv.get_feature_names_out()
    print('done')
    return tf_idf_vector, feature_names


def get_result_data(files, tf_idf_vector, feature_names):
    print("Extracting top words for files")
    docword = {}
    print(tf_idf_vector.shape[0])
    for i in range(tf_idf_vector.shape[0]):
        if i % 10000 == 1:
            print(f'Iter: {i}/{tf_idf_vector.shape[0]}')
        curr_vector = tf_idf_vector[i]
        sorted_items = sort_coords(curr_vector.tocoo())
        keywords = extract_top_n_from_vector(feature_names, sorted_items, 50)
        docword[files[i]] = Counter(keywords)

    files_names = list(docword.keys())
    idx = 0
    stack = []
    print("Counting top words for directories")
    while idx < len(files_names):
        if idx % 10000 == 1:
            print(f'Iter: {idx}/{len(files_names)}')
        curr_name = files_names[idx]
        counter = docword[curr_name]
        curr_name = curr_name[:curr_name.rfind('/')]
        idx += 1
        while curr_name != '':
            if len(stack) > 0 and curr_name == stack[-1][0]:
                counter += stack[-1][1]
                stack.pop()
            if idx < len(files_names) and files_names[idx].startswith(curr_name):
                stack.append((curr_name, counter))
                break
            else:
                docword[curr_name] = counter
                curr_name = curr_name[:curr_name.rfind('/')]
            docword['/'] = counter
    data = collections.OrderedDict(sorted(docword.items()))
    return data


def build_json(tfidf_result, output_dir):
    out_file_path = os.path.join(output_dir, f"tfidf.json")
    json_data = {"timestamp": str(datetime.datetime.utcnow()), "data": []}
    for repo, data in tfidf_result:
        repo_data = {"path": repo, 'files': []}
        for key, cnt in data.items():
            file_data = {'path': key, 'topics': [], 'probs': []}
            for word, value in cnt.most_common(5):
                file_data['topics'].append(word)
                file_data['probs'].append("{:.3f}".format(value))
            repo_data['files'].append(file_data)
        json_data['data'].append(repo_data)
    json_dump = json.dumps(json_data, indent=4)
    mkdir(output_dir)
    with open(os.path.abspath(out_file_path), "w+") as fout:
        fout.write(json_dump)


def clone_repository(repository: str, directory: str) -> None:
    if "://" in repository:
        body = repository.split("://")[1]
    else:
        raise ValueError("{repository} is not a valid link!".format(repository=repository))
    repository = "https://user:password@" + body
    os.system("git clone --quiet --depth 1 {repository} {directory}".format(repository=repository,
                                                                            directory=directory))


if __name__ == '__main__':
    parser = ArgumentParser()
    parser.add_argument("-i", "--input", required=True,
                        help="Path to the input file with a list of links to GitHub/local repo.")
    parser.add_argument("-o", "--output", required=True,
                        help="Path to the directory for storing extracted data.")
    parser.add_argument("-l", "--local", action="store_true",
                        help="If passed, switches to local repositories.")
    args = parser.parse_args()
    with open(args.input) as fin:
        repositories_list = fin.read().splitlines()
    data = []
    for repository in repositories_list:
        print(f'Repo {repository}')
        if not args.local:
            with TemporaryDirectory() as td:
                try:
                    clone_repository(repository, td)
                except ValueError:
                    print("{repository} is not a valid link!"
                          .format(repository=repository))
                    continue
            dir_path = td
        else:
            try:
                assert os.path.isdir(repository)
            except AssertionError:
                print("{repository} doesn't exist!".format(repository=repository))
                continue
            dir_path = repository
        files = get_files_list(dir_path)
        print(f'Files found: {len(files)}')
        tf_idf_vector, feature_names = tfidf(files)
        files = list(map(lambda s: s[len(dir_path):], files))
        data.append((repository, get_result_data(files, tf_idf_vector, feature_names)))
    build_json(data, args.output)
